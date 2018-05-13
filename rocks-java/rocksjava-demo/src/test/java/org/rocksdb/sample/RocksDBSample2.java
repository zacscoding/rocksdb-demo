package org.rocksdb.sample;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.springframework.core.io.ClassPathResource;

/**
 * @author zacconding
 * @Date 2018-05-13
 * @GitHub : https://github.com/zacscoding
 */
public class RocksDBSample2 {

    private static final String DB_PATH;
    private static RocksDB db;
    private static ReadOptions readOpts;

    static {
        DB_PATH = new ClassPathResource("db/test1").getPath();
        recursiveDelete(DB_PATH);

        // a static method that loads the RocksDB C++ library.
        RocksDB.loadLibrary();
    }

    public static void main(String[] args) {
        init2();
    }

    public static boolean recursiveDelete(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            //check if the file is a directory
            if (file.isDirectory()) {
                if ((file.list()).length > 0) {
                    for(String s:file.list()){
                        //call deletion of file individually
                        recursiveDelete(fileName + System.getProperty("file.separator") + s);
                    }
                }
            }

            file.setWritable(true);
            boolean result = file.delete();
            return result;
        } else {
            return false;
        }
    }

    private static void init2() {
        try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {
            // list of column family descriptors, first entry must always be default column family
            final List<ColumnFamilyDescriptor> cfDescriptors = Arrays.asList(
                new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts),
                new ColumnFamilyDescriptor("col0".getBytes(), cfOpts),
                new ColumnFamilyDescriptor("col1".getBytes(), cfOpts),
                new ColumnFamilyDescriptor("col2".getBytes(), cfOpts),
                new ColumnFamilyDescriptor("col3".getBytes(), cfOpts),
                new ColumnFamilyDescriptor("col4".getBytes(), cfOpts),
                new ColumnFamilyDescriptor("col5".getBytes(), cfOpts),
                new ColumnFamilyDescriptor("col6".getBytes(), cfOpts),
                new ColumnFamilyDescriptor("col7".getBytes(), cfOpts)
            );

            // a list which will hold the handles for the column families once the db is opened
            final List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

            try (final DBOptions options = new DBOptions()
                .setCreateIfMissing(true)
                .setCreateMissingColumnFamilies(true);
                final RocksDB db = RocksDB.open(options, DB_PATH, cfDescriptors, columnFamilyHandleList)) {
                try {
                    // do something
                    byte[] key1 = "key1".getBytes(UTF_8);
                    byte[] value1 = db.get(key1);

                    if(value1 == null) {
                        System.out.println("key` `s value is null & putting");
                        value1 = "value1".getBytes(UTF_8);
                        db.put(key1, value1);
                    }

                    byte[] find = db.get(key1);
                    if(value1 == null) {
                        System.out.println("empty");
                    } else {
                        System.out.println("Find key1 : " + new String(find));
                    }
                } finally {
                    // NOTE frees the column family handles before freeing the db
                    for (final ColumnFamilyHandle columnFamilyHandle :
                        columnFamilyHandleList) {
                        columnFamilyHandle.close();
                    }
                } // frees the db and the db options
            } catch(RocksDBException e) {
                System.out.println("RocksDBException!!");
                e.printStackTrace();
            }
        } // frees the column family options
    }



}
