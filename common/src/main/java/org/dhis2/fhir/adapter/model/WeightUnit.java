package org.dhis2.fhir.adapter.model;

/*
 * Copyright (c) 2004-2018, University of Oslo
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
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The units of weight and their conversion.
 *
 * @author volsch
 */
public enum WeightUnit
{
    GRAM( 1.0, "g" ), KILO_GRAM( 1_000.0, "kg" ), OUNCE( 28.349523125, "[oz_av]" ), POUND( 453.59237, "[lb_av]" );

    private final double gramFactor;

    private final String ucumCode;

    private static final Map<String, WeightUnit> weightUnitByUcumCode = Arrays.stream( WeightUnit.values() ).collect( Collectors.toMap( WeightUnit::getUcumCode, v -> v ) );

    @Nullable
    public static WeightUnit getByUcumCode( @Nullable String ucumCode )
    {
        return weightUnitByUcumCode.get( ucumCode );
    }

    WeightUnit( double gramFactor, String ucumCode )
    {
        this.gramFactor = gramFactor;
        this.ucumCode = ucumCode;
    }

    public double getGramFactor()
    {
        return gramFactor;
    }

    public String getUcumCode()
    {
        return ucumCode;
    }

    public double convertTo( double value, @Nonnull WeightUnit unit )
    {
        if ( unit == this )
        {
            return value;
        }
        return (value * gramFactor) / unit.gramFactor;
    }
}
