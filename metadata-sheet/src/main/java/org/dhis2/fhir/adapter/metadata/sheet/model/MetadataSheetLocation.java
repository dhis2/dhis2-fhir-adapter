package org.dhis2.fhir.adapter.metadata.sheet.model;

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

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * The location in a sheet for which a message is generated.<br>
 *
 * <b>This metadata sheet import tool is just a temporary solution
 * and may be removed in the future completely.</b>
 *
 * @author volsch
 */
public class MetadataSheetLocation implements Serializable
{
    private static final long serialVersionUID = 8014117442538496813L;

    public static final int UNDEFINED_ROW = -1;

    public static final int UNDEFINED_CELL = -1;

    private final String sheetName;

    private final int row;

    private final int cell;

    public MetadataSheetLocation( @Nonnull String sheetName, int row, int cell )
    {
        this.sheetName = sheetName;
        this.row = row;
        this.cell = cell;
    }

    public MetadataSheetLocation( @Nonnull String sheetName, int row )
    {
        this( sheetName, row, UNDEFINED_CELL );
    }

    public MetadataSheetLocation( @Nonnull String sheetName )
    {
        this( sheetName, UNDEFINED_ROW, UNDEFINED_CELL );
    }

    @Nonnull
    public String getSheetName()
    {
        return sheetName;
    }

    public int getRow()
    {
        return row;
    }

    public int getCell()
    {
        return cell;
    }

    @Override
    public String toString()
    {
        return "MetadataSheetLocation{sheetName='" + sheetName + '\'' + ", row=" + row + ", cell=" + cell + '}';
    }
}
