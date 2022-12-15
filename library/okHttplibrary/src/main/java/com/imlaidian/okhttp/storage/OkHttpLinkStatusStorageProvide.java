package com.imlaidian.okhttp.storage;

import android.content.Context;
import com.imlaidian.okhttp.model.OkHttpLinkStatusModel;
import com.imlaidian.utilslibrary.UtilsApplication;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultDetailItem;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultEachCodeInfo;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultEachCodeItem;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultEachDataItem;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultSummaryCodeItem;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultSummaryEachCodeItem;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultSummaryItem;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultTLNameItem;
import com.imlaidian.utilslibrary.utils.ArrayUtils;
import com.imlaidian.utilslibrary.utils.DateUtil;
import com.imlaidian.utilslibrary.utils.LogUtil;
import com.imlaidian.utilslibrary.utils.StringsUtils;
import com.imlaidian.utilslibrary.utils.ThreadUtils;
import com.imlaidian.utilslibrary.utilsManager.ThreadPoolManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.imlaidian.okhttp.model.OkHttpLinkStatusModel.CODE_NETTY_CLOSED;
import static com.imlaidian.okhttp.model.OkHttpLinkStatusModel.CODE_NETTY_CLOSING_OR_START;
import static com.imlaidian.okhttp.model.OkHttpLinkStatusModel.CODE_NETTY_FETCHING;
import static com.imlaidian.okhttp.model.OkHttpLinkStatusModel.CODE_NET_CONNECTING;
import static com.imlaidian.okhttp.model.OkHttpLinkStatusModel.CODE_NET_DISCONNECT;
import static com.imlaidian.utilslibrary.utils.ThreadUtils.eightHourPeriod;

/**
 * Created by zbo on 17/2/15.
 */

public class OkHttpLinkStatusStorageProvide {
    private final static String TAG = OkHttpLinkStatusStorageProvide.class.getSimpleName() ;

    private OkHttpLinkStatusStorage linkHelp;
    private static OkHttpLinkStatusStorageProvide instance =null;

    private Runnable mRunnable = null ;

    public static OkHttpLinkStatusStorageProvide getInstance(){
        if(instance ==null){
            synchronized(OkHttpLinkStatusStorageProvide.class){
                if(instance ==null){
                   instance = new OkHttpLinkStatusStorageProvide();
                }
            }
        }
        return instance;
    }

    public OkHttpLinkStatusStorageProvide() {
        LogUtil.d(TAG, "OkHttpLinkStatusStorageProvide");
        Context context = UtilsApplication.getInstance().getApplicationContext();
        linkHelp = new OkHttpLinkStatusStorage(context);
    }

    public synchronized void insert(OkHttpLinkStatusModel item){
        linkHelp.insert(item);
    }


    public synchronized void cleanNoUseData(){
        mRunnable =  new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "cleanNoUseData");
                linkHelp.cleanNoUseData();
            }
        };

        ThreadPoolManager.getInstance().fixedRate(mRunnable,10*1000, eightHourPeriod);
    }


    public  OkHttpLinkStatusStorage getLinkHep(){
        return  linkHelp ;
    }

    public synchronized ArrayList<OkHttpLinkStatusModel> getAllLinkData(){
        return  linkHelp.getAllLinkData();
    }


//    private void createAnalyseThread() {
//        new Thread() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        LogUtil.d(TAG, "createAnalyseThread");
//                        /// 首先判断时间是否错的。
//                        if (!DateUtil.isDateValid(new Date())) {
//                            ThreadUtils.sleepMinute(3);
//                            continue;
//                        }
//
//                        LogUtil.d(TAG, "analyse begin");
//                        analyseNetLinkData();
//                        ThreadUtils.sleepHour(4);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        LogUtil.d(TAG, e.toString());
//
//                        break;
//                    }
//                }
//            }
//        }.start();
//    }
//
//    public void  recordDnsError(int status){
//        OkHttpLinkStatusModel item = new OkHttpLinkStatusModel();
//        item.setLinkDate(Long.toString(System.currentTimeMillis()));
//        item.setLinkStatus(status);
//
//        insert(item);
//    }
//
//    private void analyseNetLinkData() {
//        long beginAnalyseTimestamp = getDetailItemBeginAnalyseTimestamp();
//
//        long curPreDayEndTimestamp = DateUtil.getPreDayEndTimestamp(System.currentTimeMillis());
//
//        if (beginAnalyseTimestamp < 0) {
//            ///没有数据则生成前一天的记录
//            beginAnalyseTimestamp = DateUtil.getPreDayEndTimestamp(curPreDayEndTimestamp);
//        }
//
//        /// 前一天已经统计过，则不进行统计
//        if (DateUtil.isSameDate(beginAnalyseTimestamp, curPreDayEndTimestamp)) {
//            LogUtil.d(TAG, "the same date beginAnalyseTimestamp = " + beginAnalyseTimestamp);
//            return;
//        }
//
//        while (beginAnalyseTimestamp < curPreDayEndTimestamp) {
//            long queryBeginTimestamp = beginAnalyseTimestamp;
//            long queryEndTimestamp = DateUtil.getNextDayEndTimestamp(beginAnalyseTimestamp);
//
//            LogUtil.d(TAG, "timestamp, begin " + queryBeginTimestamp + ", end " + queryEndTimestamp);
//            if (queryEndTimestamp > curPreDayEndTimestamp) {
//                break;
//            }
//
//            generateAnalyseResult((byte)0, queryBeginTimestamp, queryEndTimestamp);
//
//
//            beginAnalyseTimestamp = queryEndTimestamp + 1;
//
//
//            /// 休眠一下，避免占用过多cpu
//            ThreadUtils.sleepSecond(0.5);
//        }
//    }
//
//    private void generateAnalyseResult(byte prefixId, long beginTimestamp, long endTimestamp) {
//        long tempTimestamp = beginTimestamp + (endTimestamp - beginTimestamp) / 2;
//        String name = getLinkTableName(prefixId, tempTimestamp);
//        String prefixIden = getLinkPrefixIden(prefixId);
//        String suffixIden = getLinkSuffixIden(tempTimestamp);
//
//        QueryDbResultTLNameItem tlNameItem = linkHelp.getTLNameItem(name);
//
//        if (null == tlNameItem) {
//            tlNameItem = linkHelp.createTLNameItem(name, prefixIden, suffixIden, beginTimestamp, endTimestamp);
//            linkHelp.createTLNameTable(tlNameItem);
//        }
//
//        ArrayList<QueryDbResultEachDataItem> eachDataItems = new ArrayList<>();
//        ArrayList<OkHttpLinkStatusModel> list = linkHelp.getDetailLinkData(beginTimestamp, endTimestamp);
//
//        if (null != list && list.size() > 0) {
//            for (int i=0; i<list.size(); i++) {
//                OkHttpLinkStatusModel item = list.get(i);
//
//                QueryDbResultEachDataItem eachDataItem = new QueryDbResultEachDataItem();
//                eachDataItems.add(eachDataItem);
//                eachDataItem.setAnalyseTimestamp(System.currentTimeMillis());
//                eachDataItem.setSourceId(item.getId());
//                eachDataItem.setDataTimestamp(StringsUtils.toLong(item.getLinkDate()));
//
//                Map<Integer, String> codeMap = item.getCodeMap();
//
//                if (null != codeMap) {
//                    for (Integer key : codeMap.keySet()) {
//                        QueryDbResultDetailItem resultDetailItem = new QueryDbResultDetailItem();
//                        resultDetailItem.setAnalyseTimestamp(System.currentTimeMillis());
//                        resultDetailItem.setCode(key);
//                        resultDetailItem.setSourceId(item.getId());
//                        resultDetailItem.setDataTimestamp(StringsUtils.toLong(item.getLinkDate()));
//                        linkHelp.insertResultItem(tlNameItem, resultDetailItem);
//
//                        eachDataItem.getCodeList().add(resultDetailItem);
//                    }
//                }
//
//                /// 休眠一下，避免占用过多cpu
//                ThreadUtils.sleepSecond(0.5);
//            }
//        }
//
//        storeAnalyseResult(tlNameItem, eachDataItems);
//        tlNameItem.setStatusAnalyseFinish();
//        linkHelp.updateTLNameTable(tlNameItem);
//    }
//
//    private void storeAnalyseResult(QueryDbResultTLNameItem tlNameItem,
//                                    ArrayList<QueryDbResultEachDataItem> list) {
//        if (null == tlNameItem || null == list) {
//            return;
//        }
//
//        int codeCount = 0;
//        String resultContent = "";
//
//        //store summary content
//        QueryDbResultSummaryItem summaryItem = new QueryDbResultSummaryItem();
//        summaryItem.setName(tlNameItem.getName());
//        summaryItem.setPrefixIden(tlNameItem.getPrefixIden());
//        summaryItem.setSuffixIden(tlNameItem.getSuffixIden());
//        linkHelp.insertSummaryItem(summaryItem);
//
//        if (list.size() > 0) {
//            Map<Integer, QueryDbResultEachCodeInfo> codeMap = generateCodeMap(list);
//
//            long totalTime = 0;
//            QueryDbResultEachDataItem firstItem = ArrayUtils.getFirst(list);
//            QueryDbResultEachDataItem lastItem = ArrayUtils.getLast(list);
//            long beginTime = 0;
//            long endTime = 0;
//
//            if (null != firstItem) {
//                beginTime = firstItem.getDataTimestamp();
//
//                if (firstItem.equals(lastItem)) {
//                    endTime = DateUtil.getCurDayEndTimestamp(beginTime);
//                } else {
//                    if (null != lastItem) {
//                        endTime = lastItem.getDataTimestamp();
//                    } else {
//                        endTime = DateUtil.getCurDayEndTimestamp(beginTime);
//                    }
//                }
//
//
//                totalTime = Math.abs(endTime - beginTime);
//
//                LogUtil.d(TAG ,  "beginTime =" +beginTime  + ",endTime =" +endTime + ",totalTime ="  +totalTime );
//
//                resultContent = ("总时长(共" + list.size() + "条) ：" + DateUtil.convertTimeInterval(totalTime)) + "\n";
//                for (Integer key : codeMap.keySet()) {
//                    QueryDbResultEachCodeInfo info = codeMap.get(key);
//
//                    long useTime = info.getTotalTimestamp(endTime);
//                    String timeStr = DateUtil.convertTimeInterval(useTime);
//                    String perStr = StringsUtils.getTwoDecimal(((float)useTime / (float)totalTime) * 100);
//                    String content = "[" + info.getTitle() + "(共" + info.getList().size() + "次)] : " + timeStr + ", 占比 : " + perStr + "%";
//                    resultContent += content + "\n";
//
//                    codeCount++;
//
//                    for (QueryDbResultEachCodeItem codeItem : info.getList()) {
//                        QueryDbResultSummaryCodeItem summaryCodeItem = new QueryDbResultSummaryCodeItem();
//                        summaryCodeItem.setSummaryId(summaryItem.getId());
//                        summaryCodeItem.setCode(codeItem.getCode());
//                        if (null != codeItem.getBeginItem()) {
//                            summaryCodeItem.setBeginId(codeItem.getBeginItem().getSourceId());
//                            summaryCodeItem.setBeginDataTimestamp(codeItem.getBeginItem().getDataTimestamp());
//                        }
//
//                        if (null != codeItem.getEndItem()) {
//                            summaryCodeItem.setEndId(codeItem.getEndItem().getSourceId());
//                            summaryCodeItem.setEndDataTimestamp(codeItem.getEndItem().getDataTimestamp());
//                        }
//
//                        linkHelp.insertSummaryCodeItem(summaryCodeItem);
//                    }
//                }
//            } else {
//                resultContent = "无数据";
//            }
//        } else {
//            resultContent = "无数据";
//        }
//
//        summaryItem.setCodeCount(codeCount);
//        summaryItem.setContent(resultContent);
//        linkHelp.updateSummaryItem(summaryItem);
//    }
//
//    private void addBeginCodeInfo(QueryDbResultEachCodeInfo itemInfo, Integer key, QueryDbResultEachDataItem item) {
//        QueryDbResultEachCodeItem beginItem = new  QueryDbResultEachCodeItem() ;
//        beginItem.setCode(key);
//        beginItem.setBeginItem(item);
//        itemInfo.setCode(key);
//        itemInfo.setTitle(OkHttpLinkStatusModel.getCodeString(key));
//        itemInfo.getList().add(beginItem);
//    }
//
//    //QueryDbResultEachDataItem 每条记录 带错误码的详细 记录
//    public Map<Integer, QueryDbResultEachCodeInfo> generateCodeMap(ArrayList<QueryDbResultEachDataItem> list) {
//        Map<Integer, QueryDbResultEachCodeInfo> codeMap = new HashMap<>();
//
//        for (int i =0 ;  i<list.size() ; i++) {
//            QueryDbResultEachDataItem item  =list.get(i);
//            Map<Integer, QueryDbResultDetailItem> itemMap = item.getCodeMap();
//            for (Integer key : codeMap.keySet()) {
//                checkCodeList(codeMap ,itemMap ,item ,list , i ,key);
//            }
//
//            for (Integer key : itemMap.keySet()) {
//                if (!codeMap.containsKey(key)) {
//                    QueryDbResultEachCodeInfo itemInfo = new QueryDbResultEachCodeInfo();
//                    addBeginCodeInfo(itemInfo ,key ,item);
//
//                    if (i == list.size() - 1) {
//                        QueryDbResultEachCodeItem lastItem = ArrayUtils.getLast(itemInfo.getList());
//                        lastItem.setEndItem(item);
//                    }
//
//                    codeMap.put(key, itemInfo);
//                }
//            }
//        }
//
//
//        return codeMap;
//    }
//
//
//    private void checkCodeList( Map<Integer, QueryDbResultEachCodeInfo> codeMap ,Map<Integer, QueryDbResultDetailItem> itemMap ,QueryDbResultEachDataItem item ,ArrayList<QueryDbResultEachDataItem> list ,int i ,int key ){
//
//        QueryDbResultEachCodeInfo codeItemInfo = codeMap.get(key);
//        QueryDbResultEachCodeItem lastItem = ArrayUtils.getLast(codeItemInfo.getList());
//
//        // 检查最新的code 时 来设置 lastItem ,有了就重新设置，没有则判断最后一个的startTime code 是否一样
//        if (itemMap.containsKey(key)) {
//            LogUtil.d(TAG , "data code map  contains codeMap key  key =" +key);
//            // 存在，看是否有结束
//            if (null != lastItem.getEndItem()) {
//                LogUtil.d(TAG , "codeItemInfo lastItem  end time not null key=" +key);
//                addBeginCodeInfo(codeItemInfo ,key ,item);
//                if(i== list.size()-1){
//                    lastItem.setEndItem(item);
//                }
//            }
//        } else {
//            // 不存在
//            LogUtil.d(TAG , "data code map not contains codeMap key  key =" +key);
//
//            //判断最后一个 lastItem 结束项 code 类型 是否和 起始的item的 code  是否相同 ,相同 就 替换 ，不相同 不更改
//            if(lastItem.getEndItem()!=null){
//                boolean  isKeySame =false ;
//
//
//
//                for(Integer endKey  :lastItem.getEndItem().getCodeMap().keySet() ){
//
//                    for(Integer startKey  : lastItem.getBeginItem().getCodeMap().keySet()){
//                        if(endKey.equals(startKey)){
//                            LogUtil.d(TAG , " code  same" );
//                            isKeySame= true ;
//                        }else{
//                            LogUtil.d(TAG , " code  different" );
//                            isKeySame =false ;
//                            break;
//                        }
//                    }
//
//                }
//                //  起始和结束相同 ，直接记录 ，不相同
//                if(isKeySame){
//                    lastItem.setEndItem(item);
//                }
//
//            }else{
//                LogUtil.d(TAG , "data code map not contains codeMap EndItem = null" );
//                lastItem.setEndItem(item);
//            }
//
//
//        }
//    }
//
//    public QueryDbResultSummaryItem getSummaryItemWithTLNameItem(QueryDbResultTLNameItem tlNameItem) {
//        if (null != tlNameItem) {
//            return linkHelp.querySummaryWithName(tlNameItem.getName());
//        }
//
//        return null;
//    }
//
//    private void test() {
//        /// 获取所有表
//        ArrayList<QueryDbResultTLNameItem> list = linkHelp.getAllTLNameItems();
//        for (QueryDbResultTLNameItem item : list) {
//            /// 获取总结
//            QueryDbResultSummaryItem summaryItem = getSummaryItemWithTLNameItem(item);
//            LogUtil.d(TAG, summaryItem.getName() + "<====>" + summaryItem.getContent());
//
//            /// 获取总结列表
//            ArrayList<QueryDbResultSummaryEachCodeItem> codeItems = getSummaryEachDataList(summaryItem);
//            LogUtil.d(TAG, "");
//
////            ArrayList<QueryDbResultEachDataItem> eachDataItems = getResultAllEachDataListWithSourceId(item);
////            ArrayList<String> contentList = generateCodeContentList(eachDataItems);
////
////            LogUtil.d(TAG, contentList + "");
//        }
//    }
//
//    public ArrayList<QueryDbResultSummaryEachCodeItem> getSummaryEachDataList(QueryDbResultSummaryItem summaryItem) {
//        if (null != summaryItem) {
//            ArrayList<QueryDbResultSummaryEachCodeItem> dataList = new ArrayList<>();
//            ArrayList<QueryDbResultSummaryCodeItem> list = linkHelp.getSummaryCodeList(summaryItem.getId());
//            Map<Integer, QueryDbResultSummaryEachCodeItem> codeMap = new HashMap<>();
//
//            for (QueryDbResultSummaryCodeItem item : list) {
//                QueryDbResultSummaryEachCodeItem codeItem = codeMap.get(item.getCode());
//                if (null == codeItem) {
//                    codeItem = new QueryDbResultSummaryEachCodeItem();
//                    codeItem.setCode(item.getCode());
//                    codeItem.setTitle(OkHttpLinkStatusModel.getCodeString(item.getCode()));
//                    codeMap.put(item.getCode(), codeItem);
//                }
//
//                codeItem.getList().add(item);
//            }
//
//            for (QueryDbResultSummaryEachCodeItem temp : codeMap.values()) {
//                dataList.add(temp);
//            }
//
//            return dataList;
//        }
//
//        return null;
//    }
//
//
//    private boolean checkCodeExplainSame( Map<Integer, QueryDbResultDetailItem> itemMap , Integer lastCode ){
//
//        boolean isSame =false ;
//
//        for(Integer keyCode :itemMap.keySet()){
//
//            if(checkStatusCodeSame(keyCode ,lastCode)) {
//                isSame =true ;
//                break;
//            }
//
//        }
//
//        return  isSame ;
//
//    }
//
//
//
//    private boolean isDisconnectStatus(int status ){
//        if(status == CODE_NET_DISCONNECT || status == CODE_NETTY_CLOSED || status ==CODE_NETTY_CLOSING_OR_START){
//            return  true ;
//        }else{
//            return false ;
//        }
//    }
//
//    private boolean isConnectingStatus(int status){
//        if(status == CODE_NET_CONNECTING || status == CODE_NETTY_FETCHING){
//            return true;
//        }else{
//            return false ;
//        }
//    }
//
//    private boolean checkStatusCodeSame(int currentLinkStatus ,int lastLinkStatus){
//
//        if( isDisconnectStatus(lastLinkStatus)
//                && isDisconnectStatus(currentLinkStatus)){
//            return  true ;
//        }else if(isConnectingStatus(lastLinkStatus) && isConnectingStatus(currentLinkStatus) ){
//            return  true ;
//        }else{
//            return  false ;
//        }
//
//    }
//
//
//    private String getLinkPrefixIden(int channelId) {
//        String temp = "" + channelId;
//
//        if (temp.length() < 2) {
//            temp = "0" + temp;
//        }
//
//        return "LinkStatusResult" + temp;
//    }
//
//    public QueryDbResultTLNameItem getTLNameLastItemWithPrefixIden(){
//        String prefixIden = getLinkPrefixIden(0);
//
//        return linkHelp.getTLNameLastItemWithPrefixIden(prefixIden);
//    }
//
//
//
//
//    private String getLinkSuffixIden(long timestamp) {
//        return DateUtil.getTimestampYYYYMMDD(timestamp, "");
//    }
//
//    private String getLinkTableName(int channelId, long timestamp) {
//        return getLinkPrefixIden(channelId) + "_" + getLinkSuffixIden(timestamp);
//    }
//
//    private QueryDbResultTLNameItem getTLNameItem(String name, String prefixIden, String suffixIden,
//                                                    long beginTimestamp, long endTimestamp) {
//        return new QueryDbResultTLNameItem(name, prefixIden, suffixIden, beginTimestamp, endTimestamp);
//    }
//
//
//
//    private QueryDbResultTLNameItem createTLNameItem(String name, String prefixIden, String suffixIden,
//                                                       long beginTimestamp, long endTimestamp) {
//        return new QueryDbResultTLNameItem(name, prefixIden, suffixIden, beginTimestamp, endTimestamp);
//    }
//
//    public ArrayList<QueryDbResultTLNameItem> getTLNameList() {
//        String prefixIden = getLinkPrefixIden(0);
//
//        return linkHelp.getTLNameAllItemWithPrefixIden(prefixIden);
//    }
//
//
//
//
//
//    public ArrayList<OkHttpLinkStatusModel> getAllDetailData() {
//        return getDetailDataList( 0, System.currentTimeMillis());
//    }
//
//    public ArrayList<OkHttpLinkStatusModel> getDetailDataList( long beginTimestamp, long endTimestamp) {
//        ArrayList<OkHttpLinkStatusModel> list = linkHelp.getDetailLinkData( beginTimestamp, endTimestamp);
//        return list;
//    }
//
//
//    //分析的时候 售线分析 当前有效时间的前一天
//    private long getDetailItemBeginAnalyseTimestamp() {
//
//        long beginAnalyseTimestamp = 0;
//        String prefixIden = getLinkPrefixIden(0);
//        QueryDbResultTLNameItem resultLastItem = linkHelp.getTLNameLastItemWithPrefixIden(prefixIden);
//
//        if (null == resultLastItem) {
//            /// 未找到，则取表里面第一条数据的时间开始分析，按照每一天进行统计，00:00:00 ~ 23:59:59
//            OkHttpLinkStatusModel detailFirstItem = linkHelp.getFirstLinkData();
//            if (null == detailFirstItem) {
//                /// 说明还没有数据，直接返回成功
//                LogUtil.d(TAG, "result last item and detail first item are null");
//                return -1;
//            }
//
//            long resultPreTimestamp = StringsUtils.toLong(detailFirstItem.getLinkDate());
//            if (DateUtil.isDateValid(resultPreTimestamp)) {
//                /// 是有效时间
//                /// 前一天的23：59：59开始
//                beginAnalyseTimestamp = DateUtil.getCurDayBeginTimestamp(resultPreTimestamp)-1000;
//            } else {
//                /// 无效时间, 取出所有数据依次查询到有效时间
//                ArrayList<OkHttpLinkStatusModel> allList = linkHelp.getAllLinkData();
//                for (int i=1; i<allList.size(); i++) {
//                    OkHttpLinkStatusModel temp = allList.get(i);
//                    long tempTimestamp = StringsUtils.toLong(temp.getLinkDate());
//                    if (DateUtil.isDateValid(tempTimestamp)) {
//                        beginAnalyseTimestamp = DateUtil.getCurDayBeginTimestamp(tempTimestamp) - 1000;
//                        break;
//                    }
//                }
//            }
//        } else {
//            beginAnalyseTimestamp = resultLastItem.getEndTimestamp() + 1;
//
//            /// 如果中途断电了，需要接着继续分析
//            if (!resultLastItem.isAnalyseFinish()) {
//                /// 删除未完成的数据
//                QueryDbResultSummaryItem summaryItem = linkHelp.querySummaryWithName(resultLastItem.getName());
//                if (null != summaryItem) {
//                    linkHelp.deleteSummaryCodeItem(summaryItem.getId());
//                    linkHelp.deleteSummaryItem(summaryItem);
//                }
//            }
//
//
//            if (!DateUtil.isDateValid(beginAnalyseTimestamp)) {
//                ///时间无效，则从最后开始往前面搜索
//                ArrayList<QueryDbResultTLNameItem> list = linkHelp.getTLNameAllItemWithPrefixIden(prefixIden);
//                for (int i=list.size()-2; i>=0; i--) {
//                    QueryDbResultTLNameItem temp = list.get(i);
//                    if (!temp.isAnalyseFinish()) {
//                        beginAnalyseTimestamp = temp.getBeginTimestamp() + 1;
//
//                        QueryDbResultSummaryItem summaryItem = linkHelp.querySummaryWithName(temp.getName());
//                        if (null != summaryItem) {
//                            linkHelp.deleteSummaryCodeItem(summaryItem.getId());
//                            linkHelp.deleteSummaryItem(summaryItem);
//                        }
//
//                        continue;
//                    }
//
//                    long tempTimestamp = temp.getEndTimestamp();
//                    if (DateUtil.isDateValid(tempTimestamp)) {
//                        beginAnalyseTimestamp = tempTimestamp + 1;
//                        break;
//                    }
//                }
//            }
//        }
//
//        ///对时间作一个校正处理, 如果时间小于 2017/1/1 00:00:00, 则置为前一天
//        if (!DateUtil.isDateValid(beginAnalyseTimestamp)) {
//            LogUtil.d(TAG, "adjust analyse timestamp = " + beginAnalyseTimestamp);
//            beginAnalyseTimestamp = DateUtil.getPreDayEndTimestamp(System.currentTimeMillis());
//        }
//
//        return beginAnalyseTimestamp;
//    }


}
