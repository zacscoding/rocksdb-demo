package org.rocksdb.sample;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.rocksdb.CompressionType;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.core.io.ClassPathResource;

/**
 * @author zacconding
 * @Date 2018-05-13
 * @GitHub : https://github.com/zacscoding
 */
public class RocksDBSample {
    private static final String DB_PATH;

    static {
        // a static method that loads the RocksDB C++ library.
        RocksDB.loadLibrary();
        DB_PATH = new ClassPathResource("db/test").getPath();
    }

    public static void main(String[] args) {
        // the Options class contains a set of configurable DB options
        // that determines the behaviour of the database.
        try (final Options options = new Options().setCreateIfMissing(true)) {

            // a factory method that returns a RocksDB instance
            try (final RocksDB db = RocksDB.open(options, DB_PATH)) {

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
            }
        } catch (RocksDBException e) {
            // do some error handling
            e.printStackTrace();
        }

    }
}
