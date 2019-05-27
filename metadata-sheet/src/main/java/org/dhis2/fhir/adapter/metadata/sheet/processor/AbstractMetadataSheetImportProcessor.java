package org.dhis2.fhir.adapter.metadata.sheet.processor;

/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.dhis.converter.StringToReferenceConverter;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessageCollector;
import org.dhis2.fhir.adapter.util.NameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract base class of sheet import processors for single sheets.<br>
 *
 * <b>This metadata sheet import tool is just a temporary solution
 * and may be removed in the future completely.</b>
 *
 * @author volsch
 */
public abstract class AbstractMetadataSheetImportProcessor
{
    public static final String PROGRAM_SHEET_NAME = "Program";

    public static final int PROGRAM_REF_ROW = 7;

    public static final int PROGRAM_REF_COL = 1;

    protected static final String SEPARATOR = " ,;\r\n\t";

    protected static final String LINE_ONLY_SEPARATOR = "\r\n";

    private final StringToReferenceConverter stringToReferenceConverter = new StringToReferenceConverter();

    @Nonnull
    public abstract MetadataSheetMessageCollector process( @Nonnull Workbook workbook );

    protected boolean notEmpty( @Nonnull Row row, int cellNum )
    {
        final Cell cell = row.getCell( cellNum );

        if ( cell == null || cell.getCellType() == CellType.FORMULA )
        {
            return false;
        }

        return cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.BOOLEAN || StringUtils.isNotBlank( cell.getStringCellValue() );
    }

    @Nullable
    protected String getString( @Nonnull Sheet sheet, int rowNum, int cellNum )
    {
        if ( rowNum > sheet.getLastRowNum() )
        {
            return null;
        }

        final Row row = sheet.getRow( rowNum );

        if ( row == null )
        {
            return null;
        }

        return getString( row, cellNum );
    }

    @Nullable
    protected String getString( @Nonnull Row row, int cellNum )
    {
        if ( cellNum > row.getLastCellNum() )
        {
            return null;
        }

        final Cell cell = row.getCell( cellNum );

        if ( cell == null )
        {
            return null;
        }

        switch ( cell.getCellType() )
        {
            case NUMERIC:
                return Integer.toString( (int) cell.getNumericCellValue() );
            case BOOLEAN:
                return Boolean.toString( cell.getBooleanCellValue() );
            case FORMULA:
                return null;
            default:
                return StringUtils.trimToNull( cell.getStringCellValue() );
        }
    }

    @Nullable
    protected List<String> getStringList( @Nullable String value )
    {
        if ( value == null || StringUtils.isBlank( value ) )
        {
            return null;
        }

        return Stream.of( StringUtils.split( value, SEPARATOR ) ).collect( Collectors.toList() );
    }

    @Nullable
    protected Reference getReference( @Nullable String value )
    {
        if ( value == null )
        {
            return null;
        }

        final Set<Reference> references = getAllReferencesInternal( value );

        return references == null ? null : getReference( references );
    }

    @Nullable
    protected Set<Reference> getAllReferences( @Nullable String value )
    {
        if ( value == null )
        {
            return null;
        }

        return getAllReferencesInternal( value );
    }

    private Set<Reference> getAllReferencesInternal( @Nonnull String value )
    {
        final Set<Reference> references = new LinkedHashSet<>();

        for ( final String v : StringUtils.split( value, LINE_ONLY_SEPARATOR ) )
        {
            try
            {
                if ( !v.isEmpty() )
                {
                    final Reference reference = stringToReferenceConverter.doConvert( v.trim() );

                    references.add( reference );
                }
            }
            catch ( ConversionException e )
            {
                return null;
            }
        }

        return references;
    }

    @Nullable
    protected <T extends Enum<T>> List<T> getEnums( @Nonnull Class<T> enumClass, @Nonnull String value )
    {
        final List<T> result = new ArrayList<>();

        for ( final String v : StringUtils.split( value, SEPARATOR ) )
        {
            try
            {
                result.add( NameUtils.toEnumValue( enumClass, v.trim() ) );
            }
            catch ( IllegalArgumentException e )
            {
                return null;
            }
        }

        return result;
    }

    @Nullable
    protected Reference getProgramRef( @Nonnull Workbook workbook )
    {
        final Sheet sheet = workbook.getSheet( PROGRAM_SHEET_NAME );

        if ( sheet == null )
        {
            return null;
        }

        final String programRefValue = getString( sheet, PROGRAM_REF_ROW, PROGRAM_REF_COL );

        if ( programRefValue == null )
        {
            return null;
        }

        return getReference( programRefValue );
    }

    protected Reference getReference( @Nonnull Set<Reference> references )
    {
        final Map<ReferenceType, Reference> referenceMap = references.stream().collect( Collectors.toMap( Reference::getType, r -> r ) );

        if ( referenceMap.containsKey( ReferenceType.CODE ) )
        {
            return referenceMap.get( ReferenceType.CODE );
        }

        if ( referenceMap.containsKey( ReferenceType.ID ) )
        {
            return referenceMap.get( ReferenceType.ID );
        }

        return referenceMap.get( ReferenceType.NAME );
    }

    @Nullable
    protected Boolean getBoolean( @Nonnull Sheet sheet, int rowNum, int cellNum )
    {
        final String value = getString( sheet, rowNum, cellNum );

        if ( value == null )
        {
            return null;
        }

        return "yes".equalsIgnoreCase( value ) || "true".equalsIgnoreCase( value );
    }

    @Nonnull
    protected String createCode( @Nonnull String value )
    {
        final StringBuilder sb = new StringBuilder();

        for ( int i = 0; i < value.length(); i++ )
        {
            final char c = value.charAt( i );

            if ( Character.isLetterOrDigit( c ) )
            {
                sb.append( Character.toUpperCase( c ) );
            }
            else if ( c == ' ' || c == '-' || c == '_' )
            {
                sb.append( '_' );
            }
        }

        return sb.toString();
    }
}
