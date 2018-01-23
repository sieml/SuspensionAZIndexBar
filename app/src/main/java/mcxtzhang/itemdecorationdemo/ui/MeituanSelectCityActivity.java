package mcxtzhang.itemdecorationdemo.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import mcxtzhang.itemdecorationdemo.utils.DBManager;

import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mcxtzhang.itemdecorationdemo.R;
import mcxtzhang.itemdecorationdemo.adapter.MeituanAdapter;
import mcxtzhang.itemdecorationdemo.decoration.DividerItemDecoration;
import mcxtzhang.itemdecorationdemo.model.MeiTuanBean;
import mcxtzhang.itemdecorationdemo.model.MeituanHeaderBean;
import mcxtzhang.itemdecorationdemo.model.MeituanTopHeaderBean;
import mcxtzhang.itemdecorationdemo.utils.CommonAdapter;
import mcxtzhang.itemdecorationdemo.utils.HeaderRecyclerAndFooterWrapperAdapter;
import mcxtzhang.itemdecorationdemo.utils.ViewHolder;


/**
 * 介绍： 选择城市页面
 * 作者：zhangxutong
 * 邮箱：mcxtzhang@163.com
 * 主页：http://blog.csdn.net/zxt0601
 * 时间： 2016/11/7.
 */
public class MeituanSelectCityActivity extends AppCompatActivity {
    private static final String TAG = "zxt";
    private Context mContext;
    private RecyclerView mRv;
    private MeituanAdapter mAdapter;
    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter;
    private LinearLayoutManager mManager;
    private DBManager dbManager;
    //设置给InexBar、ItemDecoration的完整数据集
    private List<BaseIndexPinyinBean> mSourceDatas;
    //头部数据源
    private List<MeituanHeaderBean> mHeaderDatas;
    //主体部分数据源（城市数据）
    private List<MeiTuanBean> mBodyDatas;

    private SuspensionDecoration mDecoration;

    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;

    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meituan);
        mContext = this;

        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(mManager = new LinearLayoutManager(this));
        mSourceDatas = new ArrayList<>();
        mHeaderDatas = new ArrayList<>();
        mHeaderDatas.add(new MeituanHeaderBean(new ArrayList<String>(), "热门城市", "Hot"));
        mSourceDatas.addAll(mHeaderDatas);

        mAdapter = new MeituanAdapter(this, R.layout.meituan_item_select_city, mBodyDatas);
        mHeaderAdapter = new HeaderRecyclerAndFooterWrapperAdapter(mAdapter) {
            @Override
            protected void onBindHeaderHolder(ViewHolder holder, int headerPos, int layoutId, Object o) {
                switch (layoutId) {
                    case R.layout.meituan_item_header:
                        final MeituanHeaderBean meituanHeaderBean = (MeituanHeaderBean) o;
                        //网格
                        RecyclerView recyclerView = holder.getView(R.id.rvCity);
                        recyclerView.setAdapter(
                                new CommonAdapter<String>(mContext, R.layout.meituan_item_header_item, meituanHeaderBean.getCityList()) {
                                    @Override
                                    public void convert(ViewHolder holder, final String cityName) {
                                        holder.setText(R.id.tvName, cityName);
                                        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(mContext, "cityName:" + cityName, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
                        break;
                    case R.layout.meituan_item_header_top:
                        MeituanTopHeaderBean meituanTopHeaderBean = (MeituanTopHeaderBean) o;
                        holder.setText(R.id.tvCurrent, meituanTopHeaderBean.getTxt());
                        break;
                    default:
                        break;
                }
            }
        };
        mHeaderAdapter.setHeaderView(0, R.layout.title_filter, "");
        mHeaderAdapter.setHeaderView(1, R.layout.title_filter, "");
        mHeaderAdapter.setHeaderView(2, R.layout.meituan_item_header, mHeaderDatas.get(0));

        mRv.setAdapter(mHeaderAdapter);
        mRv.addItemDecoration(mDecoration = new SuspensionDecoration(this, mSourceDatas)
                .setmTitleHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics()))
                .setColorTitleBg(0xffefefef)
                .setTitleFontSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()))
                .setColorTitleFont(mContext.getResources().getColor(android.R.color.black))
                .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount() - mHeaderDatas.size()));
        mRv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));

        //使用indexBar
        mTvSideBarHint = (TextView) findViewById(R.id.tvSideBarHint);//HintTextView
        mIndexBar = (IndexBar) findViewById(R.id.indexBar);//IndexBar

        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
                .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount() - mHeaderDatas.size());

        initDatas();
    }

    private void initData() {
       /* dbManager = new DBManager(this);
        dbManager.copyDBFile();
        mBodyDatas = dbManager.getAllCities();*/
        String cities = DBManager.getJson(this, "ChinaCities.json");
        if (!TextUtils.isEmpty(cities)) {
            try {
                JSONArray array = new JSONArray(cities);
                JSONArray mCities, counties;
                JSONObject province, city, county;
                MeiTuanBean bean;
                List<MeiTuanBean> result = new ArrayList<>();
                int len0 = array.length();
                for (int i = 0; i < len0; i++) {
                    province = array.getJSONObject(i);//某省
                    mCities = province.getJSONArray("city");//省下所有城市
                    int len1 = mCities.length();
                    for (int j = 0; j < len1; j++) {
                        city = mCities.getJSONObject(j);//某城市
                        counties = city.getJSONArray("county");
                        //int len2 = counties.length();
                        for (int k = 0; k < 1; k++) {
                            county = counties.getJSONObject(k);
                            bean = new MeiTuanBean();
                            bean.setCity(county.optString("name"));
                            bean.setBaseIndexPinyin(county.optString("name_en"));
                            result.add(bean);
                        }
                    }
                }
                mBodyDatas = result;
                //先排序
                mIndexBar.getDataHelper().sortSourceDatas(mBodyDatas);

                mAdapter.setDatas(mBodyDatas);
                mHeaderAdapter.notifyDataSetChanged();
                mSourceDatas.addAll(mBodyDatas);

                mIndexBar.setmSourceDatas(mSourceDatas)//设置数据
                        .invalidate();
                mDecoration.setmDatas(mSourceDatas);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 组织数据源
     */
    private void initDatas() {

        initData();
        //延迟1秒加载头部
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                MeituanHeaderBean bean = mHeaderDatas.get(0);
                List<String> hotCitys = new ArrayList<>();
                hotCitys.add("上海");
                hotCitys.add("北京");
                hotCitys.add("广州");
                hotCitys.add("杭州");
                hotCitys.add("深圳");
                hotCitys.add("成都");
                hotCitys.add("苏州");
                hotCitys.add("南京");
                bean.setCityList(hotCitys);

                mHeaderAdapter.notifyItemRangeChanged(2, 2);
            }
        }, 1000);

    }
}
