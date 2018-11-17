package org.dhis2.fhir.adapter.scriptable.generator;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.text.WordUtils;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The controller that provides the downloadable script.
 *
 * @author volsch
 */
@ConditionalOnBean( JavaScriptGeneratorConfig.class )
@Controller
public class JavaScriptGeneratorController
{
    public static final String LICENSE_FILE = "classpath:/dhis2-license.txt";

    public static final String LINE_ENDING = "\r\n";

    public static final int MAX_COMMENT_LENGTH = 80;

    private final JavaScriptGeneratorConfig config;

    private final ResourceLoader resourceLoader;

    private String script;

    private Instant lastModified;

    private String eTag;

    public JavaScriptGeneratorController( @SuppressWarnings( "SpringJavaInjectionPointsAutowiringInspection" ) @Nonnull JavaScriptGeneratorConfig config, @Nonnull ResourceLoader resourceLoader )
    {
        this.config = config;
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping( path = "/scripts/to-dhis2-all-mapping.js", method = RequestMethod.GET, produces = "application/javascript;charset=UTF-8" )
    public ResponseEntity<String> getScript( @Nonnull WebRequest request )
    {
        if ( request.checkNotModified( eTag, lastModified.toEpochMilli() ) )
        {
            return null;
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setLastModified( lastModified.toEpochMilli() );
        headers.setETag( eTag );
        return new ResponseEntity<>( script, headers, HttpStatus.OK );
    }

    @PostConstruct
    protected void init()
    {
        lastModified = Instant.now();

        final SortedMap<String, Class<?>> orderedClasses = new TreeMap<>();
        final Set<ScriptType> processedAnnotations = new HashSet<>();
        final List<ScriptTypeInfo> scriptTypeInfoList = new ArrayList<>();
        findAllScriptTypes().forEach( c -> {
            final ScriptType scriptType = AnnotationUtils.findAnnotation( c, ScriptType.class );
            if ( (scriptType != null) && processedAnnotations.add( scriptType ) )
            {
                if ( orderedClasses.put( scriptType.value(), c ) != null )
                {
                    throw new JavaScriptGeneratorConfigException( "Duplicate script type: " + c );
                }
                scriptTypeInfoList.add( new ScriptTypeInfo( c, scriptType ) );
            }
        } );

        final StringBuilder sb = new StringBuilder( createCommentedLicense() );
        scriptTypeInfoList.forEach( scriptTypeInfo -> {
            final TypeInfo typeInfo = createTypeInfo( scriptTypeInfo.getAnnotatedClass() );
            final ScriptType st = scriptTypeInfo.getScriptType();

            sb.append( "/**" ).append( LINE_ENDING );
            Arrays.stream( st.description().split( "\n" ) )
                .forEach( l -> sb.append( " * " ).append( WordUtils.wrap( l, MAX_COMMENT_LENGTH, LINE_ENDING + " * ", false ) ) );
            sb.append( LINE_ENDING ).append( " */" ).append( LINE_ENDING );
            sb.append( "var " ).append( st.value() ).append( " = (function ()" ).append( LINE_ENDING ).append( "{" ).append( LINE_ENDING );
            sb.append( "  function " ).append( st.value() ).append( "()" ).append( LINE_ENDING ).append( "  {" ).append( LINE_ENDING );
            typeInfo.getPropertyInfo().forEach( pi -> {
                if ( StringUtils.isNotBlank( pi.getScriptMethod().description() ) )
                {
                    sb.append( "    /**" ).append( LINE_ENDING );
                    Arrays.stream( pi.getScriptMethod().description().split( "\n" ) )
                        .forEach( l -> sb.append( "     * " ).append( WordUtils.wrap( l, MAX_COMMENT_LENGTH, LINE_ENDING + "     * ", false ) ) );
                    sb.append( LINE_ENDING ).append( "     */" ).append( LINE_ENDING );
                }
                sb.append( "    this." ).append( pi.getName() ).append( " = null;" ).append( LINE_ENDING ).append( LINE_ENDING );
            } );
            sb.append( "  }" ).append( LINE_ENDING ).append( LINE_ENDING );

            typeInfo.getMethodInfo().forEach( mi -> {
                sb.append( "  /**" ).append( LINE_ENDING );
                if ( StringUtils.isNotBlank( mi.getScriptMethod().description() ) )
                {
                    Arrays.stream( mi.getScriptMethod().description().split( "\n" ) )
                        .forEach( l -> sb.append( "   * " ).append( WordUtils.wrap( l, MAX_COMMENT_LENGTH, LINE_ENDING + "   * ", false ) ) );
                    sb.append( LINE_ENDING );
                    if ( !mi.getArgs().isEmpty() || !mi.isVoidMethod() )
                    {
                        sb.append( "   * " ).append( LINE_ENDING );
                    }
                }
                if ( !mi.getArgs().isEmpty() )
                {
                    final int max = mi.getArgs().stream().map( a -> a.getName().length() ).max( Comparator.naturalOrder() ).orElse( 0 );
                    final int tab = max + 4;
                    mi.getArgs().forEach( a -> {
                        String description = "";
                        if ( (a.getScriptMethodArg() != null) && StringUtils.isNotBlank( a.getScriptMethodArg().description() ) )
                        {
                            description = a.getScriptMethodArg().description();
                        }
                        else if ( mi.isProperty() )
                        {
                            description = mi.getScriptMethod().description();
                        }
                        sb.append( "   * @param " ).append( a.getName() ).append( StringUtils.repeat( ' ', max - a.getName().length() ) ).append( ' ' );
                        Arrays.stream( description.split( "\n" ) ).filter( StringUtils::isNotBlank )
                            .forEach( l -> sb.append( WordUtils.wrap( l, MAX_COMMENT_LENGTH - tab, LINE_ENDING + "   *     " + StringUtils.repeat( ' ', tab ), false ) ) );
                        sb.append( LINE_ENDING );
                    } );
                }
                if ( !mi.isVoidMethod() )
                {
                    String returnDescription = "";
                    if ( StringUtils.isNotBlank( mi.getScriptMethod().returnDescription() ) )
                    {
                        returnDescription = mi.getScriptMethod().returnDescription();
                    }
                    else if ( mi.isProperty() )
                    {
                        returnDescription = mi.getScriptMethod().description();
                    }
                    sb.append( "   * @return " );
                    Arrays.stream( returnDescription.split( "\n" ) ).filter( StringUtils::isNotBlank )
                        .forEach( l -> sb.append( WordUtils.wrap( l, MAX_COMMENT_LENGTH - 8, LINE_ENDING + "   *         ", false ) ) );
                    sb.append( LINE_ENDING );
                }
                sb.append( "   */" ).append( LINE_ENDING );
                sb.append( "  " ).append( st.value() ).append( ".prototype." ).append( mi.getName() ).append( " = function (" );
                boolean first = true;
                for ( final MethodArgInfo a : mi.getArgs() )
                {
                    if ( first )
                    {
                        first = false;
                    }
                    else
                    {
                        sb.append( ", " );
                    }
                    sb.append( a.getName() );
                }
                sb.append( ") {};" ).append( LINE_ENDING ).append( LINE_ENDING );
            } );

            sb.append( "  return " ).append( st.value() ).append( ";" ).append( LINE_ENDING );
            sb.append( "}());" ).append( LINE_ENDING );
            if ( StringUtils.isNotBlank( st.var() ) )
            {
                sb.append( "var " ).append( st.var() ).append( " = new " ).append( st.value() ).append( "();" ).append( LINE_ENDING );
            }
            sb.append( LINE_ENDING );
        } );

        script = sb.toString();
        eTag = "\"" + createSha1( script ) + "\"";
    }

    @Nonnull
    private String createCommentedLicense()
    {
        final StringBuilder sb = new StringBuilder( "/*" ).append( LINE_ENDING );
        try ( final LineNumberReader r = new LineNumberReader( new InputStreamReader( resourceLoader.getResource( LICENSE_FILE ).getInputStream(), StandardCharsets.UTF_8 ) ) )
        {
            String line;
            while ( (line = r.readLine()) != null )
            {
                sb.append( " * " ).append( line ).append( LINE_ENDING );
            }
        }
        catch ( IOException e )
        {
            throw new JavaScriptGeneratorConfigException( "Could not load license file " + LICENSE_FILE );
        }
        return sb.append( " */" ).append( LINE_ENDING ).append( LINE_ENDING ).toString();
    }

    @Nonnull
    private Set<Class<?>> findAllScriptTypes()
    {
        final ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider( false );
        componentProvider.setResourceLoader( resourceLoader );
        componentProvider.addIncludeFilter( new AnnotationTypeFilter( ScriptType.class, false, true ) );

        final Set<Class<?>> scriptTypeClasses = new HashSet<>();
        Arrays.stream( config.getBasePackageClasses() ).map( c -> c.getPackage().getName() ).forEach( packageName -> componentProvider.findCandidateComponents( packageName ).forEach( c -> {
            try
            {
                scriptTypeClasses.add( Class.forName( c.getBeanClassName(), false, resourceLoader.getClassLoader() ) );
            }
            catch ( ClassNotFoundException e )
            {
                // can only happen if there is a big misconfiguration with the class loader
                throw new JavaScriptGeneratorConfigException( "Could not load Spring scanned class " + c.getBeanClassName(), e );
            }
        } ) );

        return scriptTypeClasses;
    }

    @Nonnull
    private TypeInfo createTypeInfo( @Nonnull Class<?> c )
    {
        final Map<String, PropertyInfo> properties = new HashMap<>();
        final Map<String, MethodInfo> methods = new HashMap<>();
        MethodUtils.getMethodsListWithAnnotation( c, ScriptMethod.class, true, false ).stream().distinct().forEach( m -> {
            final ScriptMethod scriptMethod = m.getAnnotation( ScriptMethod.class );
            final String name = m.getName();
            boolean property = false;
            if ( name.startsWith( "is" ) && (name.length() > 2) && (m.getParameterCount() == 0) && boolean.class.equals( m.getReturnType() ) )
            {
                properties.computeIfAbsent( StringUtils.uncapitalize( name.substring( 2 ) ), k -> new PropertyInfo( k, scriptMethod ) ).setRead( scriptMethod );
                property = true;
            }
            else if ( name.startsWith( "get" ) && (name.length() > 3) && (m.getParameterCount() == 0) && !Void.class.equals( m.getReturnType() ) )
            {
                properties.computeIfAbsent( StringUtils.uncapitalize( name.substring( 3 ) ), k -> new PropertyInfo( k, scriptMethod ) ).setRead( scriptMethod );
                property = true;
            }
            else if ( name.startsWith( "set" ) && (name.length() > 3) && (m.getParameterCount() == 1) )
            {
                properties.computeIfAbsent( StringUtils.uncapitalize( name.substring( 3 ) ), k -> new PropertyInfo( k, scriptMethod ) ).setWrite( scriptMethod );
                property = true;
            }

            final Map<String, ScriptMethodArg> scriptMethodArgs = Arrays.stream( scriptMethod.args() ).collect( Collectors.toMap( ScriptMethodArg::value, a -> a ) );
            final List<MethodArgInfo> args = new ArrayList<>();
            Arrays.stream( m.getParameters() ).forEach( p -> {
                if ( !p.isNamePresent() )
                {
                    throw new JavaScriptGeneratorConfigException( "Could not determine name of method parameters: " + m );
                }
                final String n = p.getName();
                args.add( new MethodArgInfo( n, scriptMethodArgs.remove( n ) ) );
            } );
            if ( !scriptMethodArgs.isEmpty() )
            {
                throw new JavaScriptGeneratorConfigException( "Method " + name + " contains unused script method argument annotations: " + scriptMethodArgs.keySet() );
            }

            if ( methods.put( name + ":" + m.getParameterCount(), new MethodInfo( name, scriptMethod, property, void.class.equals( m.getReturnType() ), args ) ) != null )
            {
                throw new JavaScriptGeneratorConfigException( "Class " + c.getName() + " contains multiple methods named " + name + " (" + m.getParameterCount() + ")" );
            }
        } );
        return new TypeInfo( new TreeSet<>( properties.values() ), new TreeSet<>( methods.values() ) );
    }

    @Nonnull
    private String createSha1( @Nonnull String value )
    {
        try
        {
            final MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
            return Base64.getEncoder().withoutPadding().encodeToString( digest.digest( value.getBytes( StandardCharsets.UTF_8 ) ) );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new JavaScriptGeneratorConfigException( e.getMessage(), e );
        }
    }

    private static class ScriptTypeInfo
    {
        private final Class<?> annotatedClass;

        private final ScriptType scriptType;

        public ScriptTypeInfo( @Nonnull Class<?> annotatedClass, @Nonnull ScriptType scriptType )
        {
            this.annotatedClass = annotatedClass;
            this.scriptType = scriptType;
        }

        @Nonnull
        public Class<?> getAnnotatedClass()
        {
            return annotatedClass;
        }

        @Nonnull
        public ScriptType getScriptType()
        {
            return scriptType;
        }
    }
}
