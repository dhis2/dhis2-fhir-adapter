package org.dhis2.fhir.adapter.metadata.sheet.controller;

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

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessage;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessageSeverity;
import org.dhis2.fhir.adapter.metadata.sheet.processor.MetadataSheetImportException;
import org.dhis2.fhir.adapter.metadata.sheet.processor.MetadataSheetImportProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Controller that can import metadata from an Excel sheet.<br>
 *
 * <b>This metadata sheet import tool is just a temporary solution
 * and may be removed in the future completely.</b>
 *
 * @author volsch
 */
@Controller
@PreAuthorize( "hasRole('CODE_MAPPING') and hasRole('DATA_MAPPING')" )
public class MetadataSheetImportController
{
    private final Logger log = LoggerFactory.getLogger( getClass() );

    private final MetadataSheetImportProcessor importProcessor;

    public MetadataSheetImportController( @Nonnull MetadataSheetImportProcessor importProcessor )
    {
        this.importProcessor = importProcessor;
    }

    @Nonnull
    @GetMapping( "/app/metadata-sheet" )
    public String display( @Nonnull Model model, @Nonnull HttpServletRequest servletRequest )
    {
        model.addAttribute( "processedSheet", false );
        return "metadata-sheet";
    }

    @Nonnull
    @PostMapping( value = "/app/metadata-sheet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public String imp( @Nonnull @RequestParam MultipartFile file, @Nonnull Model model )
    {
        final MetadataSheetImportResult result = new MetadataSheetImportResult();

        try ( final XSSFWorkbook workbook = XSSFWorkbookFactory.createWorkbook( file.getInputStream() ) )
        {
            result.add( importProcessor.process( workbook ) );
            result.setSuccess( true );
        }
        catch ( MetadataSheetImportException e )
        {
            log.debug( "Error while importing metadata.", e );
            result.add( e.getMessageCollector() );
        }
        catch ( InvalidFormatException e )
        {
            log.debug( "The Excel file has an invalid format.", e );
            result.addMessage( new MetadataSheetMessage( MetadataSheetMessageSeverity.FATAL, "The Excel file has an invalid format: " + e.getMessage() ) );
        }
        catch ( IOException e )
        {
            log.debug( "Could not read Excel file.", e );
            result.addMessage( new MetadataSheetMessage( MetadataSheetMessageSeverity.FATAL, "Could not read Excel file: " + e.getMessage() ) );
        }
        catch ( Exception e )
        {
            log.error( "Error while importing metadata.", e );
            result.addMessage( new MetadataSheetMessage( MetadataSheetMessageSeverity.FATAL, "Error while importing metadata: " + e.getMessage() ) );
        }

        model.addAttribute( "processedSheet", true );
        model.addAttribute( "importResult", result );
        return "metadata-sheet";
    }
}
