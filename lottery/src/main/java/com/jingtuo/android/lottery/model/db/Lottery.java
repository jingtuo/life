package com.jingtuo.android.lottery.model.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import lombok.Data;

/**
 * 彩票
 *
 * @author JingTuo
 */
@Entity(indices = {@Index(value = {"descr", "notes", "area"})})
@Data
public class Lottery implements Parcelable {

    /**
     * 彩票种类
     * 如：k3
     */
    private String series;

    /**
     * 所属地区
     * 如：上海
     */
    private String area;

    /**
     * 彩票分类
     * 如：福彩
     */
    private String issuer;

    /**
     * 每天开奖次数，零为不定
     * 如：85
     */
    private String times;

    /**
     * true或false，表式是否热门彩票
     */
    private String hots;

    /**
     * 是否高频彩票
     */
    private String high;

    /**
     * 彩票的code，查询彩票开奖信息时用
     * 如：shk3
     */
    @NonNull
    @PrimaryKey
    private String code;

    /**
     * 彩票描述
     */
    private String notes;

    /**
     * 彩票名称
     */
    @SerializedName("descr")
    private String descr;

    public Lottery(@NonNull String code) {
        this.code = code;
    }

    protected Lottery(Parcel in) {
        series = in.readString();
        area = in.readString();
        issuer = in.readString();
        times = in.readString();
        hots = in.readString();
        high = in.readString();
        code = Objects.requireNonNull(in.readString());
        notes = in.readString();
        descr = in.readString();
    }

    public static final Creator<Lottery> CREATOR = new Creator<Lottery>() {
        @Override
        public Lottery createFromParcel(Parcel in) {
            return new Lottery(in);
        }

        @Override
        public Lottery[] newArray(int size) {
            return new Lottery[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(series);
        dest.writeString(area);
        dest.writeString(issuer);
        dest.writeString(times);
        dest.writeString(hots);
        dest.writeString(high);
        dest.writeString(code);
        dest.writeString(notes);
        dest.writeString(descr);
    }
}
