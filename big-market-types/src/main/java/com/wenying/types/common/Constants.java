package com.wenying.types.common;

/**
 * 定义一些Redis键前缀
 */
public class Constants {

    public final static String SPLIT = ",";
    public final static String COLON = ":";
    public final static String SPACE = " ";
    public final static String UNDERLINE = "_";


    public static class RedisKey {
        public static String STRATEGY_KEY = "big_market_strategy_key_";
        public static String STRATEGY_AWARD_KEY = "big_market_strategy_award_key_";
        public static String STRATEGY_RATE_TABLE_KEY = "big_market_strategy_rate_table_key_";
        public static String STRATEGY_RATE_RANGE_KEY = "big_market_strategy_rate_range_key_";
        public static String RULE_TREE_VO_KEY = "rule_tree_vo_key_";
        public static String STRATEGY_AWARD_COUNT_KEY = "strategy_award_count_key";//策略奖品数量缓存的key
        public static String STRATEGY_AWARD_COUNT_QUEUE_KEY = "strategy_award_count_queue_key";//奖品信息写在队列里
    }

}
