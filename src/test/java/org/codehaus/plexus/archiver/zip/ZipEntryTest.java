package org.codehaus.plexus.archiver.zip;

/*
 * Copyright  2001-2002,2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import junit.framework.TestCase;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

/**
 * JUnit 3 testcases for org.apache.tools.zip.ZipEntry.
 *
 * @version $Revision$ $Date$
 *          from org.apache.ant.tools.zip.ZipEntryTest v1.9
 */
public class ZipEntryTest
    extends TestCase
{

    public ZipEntryTest( String name )
    {
        super( name );
    }

    /**
     * test handling of extra fields
     *
     * @since 1.1
     */
    public void testExtraFields()
    {
        org.apache.commons.compress.archivers.zip.AsiExtraField a = new org.apache.commons.compress.archivers.zip.AsiExtraField();
        a.setDirectory( true );
        a.setMode( 0755 );
        org.apache.commons.compress.archivers.zip.UnrecognizedExtraField u = new org.apache.commons.compress.archivers.zip.UnrecognizedExtraField();
        u.setHeaderId( new org.apache.commons.compress.archivers.zip.ZipShort( 1 ) );
        u.setLocalFileDataData( new byte[0] );

        ZipArchiveEntry ze = new ZipArchiveEntry( "test/" );
        ze.setExtraFields( new org.apache.commons.compress.archivers.zip.ZipExtraField[]{a, u} );
        byte[] data1 = ze.getExtra();
        org.apache.commons.compress.archivers.zip.ZipExtraField[] result = ze.getExtraFields();
        assertEquals( "first pass", 2, result.length );
        assertSame( a, result[ 0 ] );
        assertSame( u, result[ 1 ] );

        org.apache.commons.compress.archivers.zip.UnrecognizedExtraField u2 = new org.apache.commons.compress.archivers.zip.UnrecognizedExtraField();
        u2.setHeaderId( new org.apache.commons.compress.archivers.zip.ZipShort( 1 ) );
        u2.setLocalFileDataData( new byte[]{1} );

        ze.addExtraField( u2 );
        byte[] data2 = ze.getExtra();
        result = ze.getExtraFields();
        assertEquals( "second pass", 2, result.length );
        assertSame( a, result[ 0 ] );
        assertSame( u2, result[ 1 ] );
        assertEquals( "length second pass", data1.length + 1, data2.length );

        org.apache.commons.compress.archivers.zip.UnrecognizedExtraField u3 = new org.apache.commons.compress.archivers.zip.UnrecognizedExtraField();
        u3.setHeaderId( new org.apache.commons.compress.archivers.zip.ZipShort( 2 ) );
        u3.setLocalFileDataData( new byte[]{1} );
        ze.addExtraField( u3 );
        result = ze.getExtraFields();
        assertEquals( "third pass", 3, result.length );

        ze.removeExtraField( new org.apache.commons.compress.archivers.zip.ZipShort( 1 ) );
        byte[] data3 = ze.getExtra();
        result = ze.getExtraFields();
        assertEquals( "fourth pass", 2, result.length );
        assertSame( a, result[ 0 ] );
        assertSame( u3, result[ 1 ] );
        assertEquals( "length fourth pass", data2.length, data3.length );

        try
        {
            ze.removeExtraField( new org.apache.commons.compress.archivers.zip.ZipShort( 1 ) );
            fail( "should be no such element" );
        }
        catch ( java.util.NoSuchElementException nse )
        {
        }
    }

    public void testUnixMode()
    {
        ZipArchiveEntry ze = new ZipArchiveEntry( "foo" );
        assertEquals( 0, ze.getPlatform() );
        ze.setUnixMode( 0755 );
        assertEquals( 3, ze.getPlatform() );
        assertEquals( 0755,
                      ( ze.getExternalAttributes() >> 16 ) & 0xFFFF );
        assertEquals( 0, ze.getExternalAttributes() & 0xFFFF );

        ze.setUnixMode( 0444 );
        assertEquals( 3, ze.getPlatform() );
        assertEquals( 0444,
                      ( ze.getExternalAttributes() >> 16 ) & 0xFFFF );
        assertEquals( 1, ze.getExternalAttributes() & 0xFFFF );

        ze = new ZipArchiveEntry( "foo/" );
        assertEquals( 0, ze.getPlatform() );
        ze.setUnixMode( 0777 );
        assertEquals( 3, ze.getPlatform() );
        assertEquals( 0777,
                      ( ze.getExternalAttributes() >> 16 ) & 0xFFFF );
        assertEquals( 0x10, ze.getExternalAttributes() & 0xFFFF );

        ze.setUnixMode( 0577 );
        assertEquals( 3, ze.getPlatform() );
        assertEquals( 0577,
                      ( ze.getExternalAttributes() >> 16 ) & 0xFFFF );
        assertEquals( 0x11, ze.getExternalAttributes() & 0xFFFF );
    }

}
