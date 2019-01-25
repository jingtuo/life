package com.jingtuo.android.lottery.model.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

/**
 * 数据库从1升到2
 * @author JingTuo
 */
public class Migration1To2 extends Migration {
    /**
     * Creates a new migration between {@code startVersion} and {@code endVersion}.
     *
     * @param startVersion The start version of the database.
     * @param endVersion   The end version of the database after this migration is applied.
     */
    public Migration1To2(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `LotteryCombination` (" +
                "`code` TEXT NOT NULL," +
                "`combination` TEXT NOT NULL," +
                "`probability` REAL NOT NULL," +
                "PRIMARY KEY(`code`, `combination`))");
    }
}
