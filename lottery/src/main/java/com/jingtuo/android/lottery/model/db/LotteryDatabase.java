package com.jingtuo.android.lottery.model.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * 彩票数据
 * @author JingTuo
 */
@Database(entities = {Lottery.class, LotteryResult.class, LotteryCombination.class}, version = 2)
public abstract class LotteryDatabase extends RoomDatabase {
    public abstract LotteryDao lotteryDao();
}
