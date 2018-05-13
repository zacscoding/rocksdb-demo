package org.rocksdb.sample;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.BloomFilter;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.CompressionType;
import org.rocksdb.DBOptions;
import org.rocksdb.Options;
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
public class ReadPairtyDB {

    private static final String DB_PATH;
    private static RocksDB db;
    private static ReadOptions readOpts;

    static {
        // a static method that loads the RocksDB C++ library.
        RocksDB.loadLibrary();
        DB_PATH = new ClassPathResource("db/parity").getPath();
    }

    public static void main(String[] args) throws RocksDBException {
        // init();
        init2();
    }

    private static void init() {
        try (Options options = new Options()) {
            // general options
            options.setCreateIfMissing(true);
            /*
            NO_COMPRESSION((byte)0, (String)null),
            SNAPPY_COMPRESSION((byte)1, "snappy"),
            ZLIB_COMPRESSION((byte)2, "z"),
            BZLIB2_COMPRESSION((byte)3, "bzip2"),
            LZ4_COMPRESSION((byte)4, "lz4"),
            LZ4HC_COMPRESSION((byte)5, "lz4hc"),
            XPRESS_COMPRESSION((byte)6, "xpress"),
            ZSTD_COMPRESSION((byte)7, "zstd"),
            DISABLE_COMPRESSION_OPTION((byte)127, (String)null);
             */
            // options.setCompressionType(CompressionType.NO_COMPRESSION);
            // options.setCompressionType(CompressionType.SNAPPY_COMPRESSION);
            // options.setCompressionType(CompressionType.ZLIB_COMPRESSION);
            // options.setCompressionType(CompressionType.BZLIB2_COMPRESSION);
            // options.setCompressionType(CompressionType.LZ4_COMPRESSION);
            // options.setCompressionType(CompressionType.LZ4HC_COMPRESSION);
            // options.setCompressionType(CompressionType.XPRESS_COMPRESSION);
            // options.setCompressionType(CompressionType.ZSTD_COMPRESSION);
            // options.setCompressionType(CompressionType.DISABLE_COMPRESSION_OPTION);


            // options.setBottommostCompressionType(CompressionType.ZSTD_COMPRESSION);
            options.setLevelCompactionDynamicLevelBytes(true);
            // options.setMaxOpenFiles(settings.getMaxOpenFiles());
            // options.setIncreaseParallelism(settings.getMaxThreads());

            // key prefix for state node lookups
            // options.useFixedLengthPrefixExtractor(NodeKeyCompositor.PREFIX_BYTES);

            // table options
            final BlockBasedTableConfig tableCfg;
            options.setTableFormatConfig(tableCfg = new BlockBasedTableConfig());
            tableCfg.setBlockSize(16 * 1024);
            tableCfg.setBlockCacheSize(32 * 1024 * 1024);
            tableCfg.setCacheIndexAndFilterBlocks(true);
            tableCfg.setPinL0FilterAndIndexBlocksInCache(true);
            tableCfg.setFilter(new BloomFilter(10, false));

            // read options
            readOpts = new ReadOptions().setPrefixSameAsStart(true).setVerifyChecksums(false);
            db = RocksDB.open(options, DB_PATH);
            try (RocksIterator iterator = db.newIterator()) {
                Set<byte[]> result = new HashSet<>();

                for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                    result.add(iterator.key());
                }

                System.out.println("<~ RocksDbDataSource.keys(): " + result.size());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (RocksDBException e) {
            e.printStackTrace();
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
                    int itr = 0;
                    RocksIterator iterator = db.newIterator();
                    for (iterator.seekToFirst(); iterator.isValid(); iterator.next(), itr++) {
                        byte[] key = iterator.key();
                        byte[] val = iterator.value();
                    }
                    System.out.println("itr : " + itr);
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
