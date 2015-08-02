/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.albatross.girish.albatrosskaraoke;

/**
 *
 * @author Girish
 */

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

class KFNDumper
{
    String root = Environment.getExternalStorageDirectory().toString();
    public static final int TYPE_SONGTEXT = 1;
    public static final int TYPE_MUSIC = 2;
    public static final int TYPE_IMAGE = 3;
    public static final int TYPE_FONT = 4;
    public static final int TYPE_VIDEO = 5;
 
    // KFN file; must be seekable
    private RandomAccessFile m_file = null;
 
    class Entry
    {
        public int type;
        public String filename;
        public int length1;
        public int length2;
        public int offset;
        public int flags;
    };
 
    public KFNDumper(String fontFilename) throws IOException
    {
        m_file = new RandomAccessFile( fontFilename, "r" );
    }
 
    public List<Entry> list() throws IOException
    {
        List<Entry> files = new ArrayList<Entry> ();
 
        // Read the file signature
        String signature = new String( readBytes(4) );
 
        if ( !signature.equals("KFNB") )
            return new ArrayList<Entry> ();
 
        // Parse the header fields
        while ( true )
        {
            signature = new String( readBytes(4) );
            int type = readByte();
            int len_or_value = readDword();
 
            switch ( type )
            {
                case 1:
                    break;
 
                case 2:
                    byte[] buf = readBytes( len_or_value );
                    break;
            }
 
            if ( signature.equals("ENDH") )
                break;
        }
 
        // Read the number of files in the directory
        int numFiles = readDword();
 
        // Parse the directory
        for ( int i = 0; i < numFiles; i++ )
        {
            Entry entry = new Entry();
 
            int filenameLen = readDword();
            byte[] filename = readBytes( filenameLen );
 
            // This is definitely not correct as the native encoding is used, but that's the best we can come out with
            entry.filename = Charset.forName( "UTF-8" ).decode( ByteBuffer.wrap( filename ) ).toString();
 
            entry.type = readDword();
            entry.length1 = readDword();
            entry.offset = readDword();
            entry.length2 = readDword();
            entry.flags = readDword();
 
            files.add( entry );
        }
 
        // Since all the offsets are based on the end of directory, readjust them
        for ( int i = 0; i < files.size(); i++ )
            files.get(i).offset += m_file.getFilePointer();
 
        return files;
    }
 
    public void extract( final Entry entry, String outfilename ) throws IOException
    {

        //File theDir = new File("/storage/emulated/0/AlbatrossKaraoke/"); ---------
        File theDir = new File(root + "/AlbatrossKaraoke/");
        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + root + "/AlbatrossKaraoke/");
            boolean result = false;

            try{
                theDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            if(result) {
                System.out.println("DIR created");
            }
        }

        // Seek to the file beginning
        m_file.seek( entry.offset );
 
        // Create the output file
        //FileOutputStream output = new FileOutputStream( "/storage/emulated/0/AlbatrossKaraoke/" + outfilename ); ---------
        FileOutputStream output = new FileOutputStream(root + "/AlbatrossKaraoke/" + outfilename );
        byte[] buffer = new byte[8192];
        int totalRead = 0;
 
        while ( totalRead < entry.length1 )
        {
            int toRead = buffer.length;
 
            if ( toRead > entry.length1 - totalRead )
                toRead = entry.length1 - totalRead;
 
            int bytesRead = m_file.read( buffer, 0, toRead );
            output.write( buffer, 0, bytesRead );
            totalRead += bytesRead;
        }
 
        output.close();
    }
 
    // Helper I/O functions
    private int readByte() throws IOException
    {
        return m_file.read() & 0xFF;
    }
 
    private int readDword() throws IOException
    {
        int b1 = readByte();
        int b2 = readByte();
        int b3 = readByte();
        int b4 = readByte();
 
        return b4 << 24 | b3 << 16 | b2 << 8 | b1;
    }
 
    private byte [] readBytes( int length ) throws IOException
    {
        byte [] array = new byte [ length ];
 
        if ( m_file.read( array ) != length )
            throw new IOException();
 
        return array;
    }
 
    public static String call( String [] args ) throws Exception
    {
        if ( args.length == 0 )
        {
            System.out.println( "Usage: app <KFN file>\n" );
            return null;
        }
 
        KFNDumper kfnfile = new KFNDumper( args[0] );
        Log.e("args", kfnfile.toString());
        List<Entry> entries = kfnfile.list();

        String temp = null;
        for ( Entry entry : entries )
        {
            if(entry.type == 2)
            {
                temp = entry.filename;
                Log.e("entry filename", temp);
            }
            System.out.println( "File " + entry.filename + ", type: " + entry.type + ", length1: "
                                           + entry.length1 + ", length2: " + entry.length2 + ", offset: "
                                           + entry.offset + ", flags: " + entry.flags  );
            kfnfile.extract( entry, entry.filename );
        }
        return temp;
    }
}