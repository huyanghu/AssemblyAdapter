package me.xiaopan.assemblyadaptersample.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.assemblyadapter.FixedRecyclerItemInfo;
import me.xiaopan.assemblyadapter.OnRecyclerLoadMoreListener;
import me.xiaopan.assemblyadaptersample.R;
import me.xiaopan.assemblyadaptersample.bean.Game;
import me.xiaopan.assemblyadaptersample.bean.User;
import me.xiaopan.assemblyadaptersample.itemfactory.GameRecyclerItemFactory;
import me.xiaopan.assemblyadaptersample.itemfactory.HeaderRecyclerItemFactory;
import me.xiaopan.assemblyadaptersample.itemfactory.LoadMoreRecyclerItemFactory;
import me.xiaopan.assemblyadaptersample.itemfactory.UserRecyclerItemFactory;

public class RecyclerViewFragment extends Fragment implements OnRecyclerLoadMoreListener {
    private int nextStart;
    private int size = 20;

    private AssemblyRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private FixedRecyclerItemInfo headerItemInfo;
    private FixedRecyclerItemInfo headerItemInfo2;
    private FixedRecyclerItemInfo footerItemInfo;
    private FixedRecyclerItemInfo footerItemInfo2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.list_recyclerViewFragment_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        } else {
            loadData();
        }
    }

    private void loadData() {
        new AsyncTask<String, String, List<Object>>() {

            @Override
            protected List<Object> doInBackground(String... params) {
                int index = 0;
                List<Object> dataList = new ArrayList<Object>(size);
                boolean userStatus = true;
                boolean gameStatus = true;
                while (index < size) {
                    if (index % 2 == 0) {
                        Game game = new Game();
                        game.iconResId = R.mipmap.ic_launcher;
                        game.name = "英雄联盟" + (index + nextStart + 1);
                        game.like = gameStatus ? "不喜欢" : "喜欢";
                        dataList.add(game);
                        gameStatus = !gameStatus;
                    } else {
                        User user = new User();
                        user.headResId = R.mipmap.ic_launcher;
                        user.name = "王大卫" + (index + nextStart + 1);
                        user.sex = userStatus ? "男" : "女";
                        user.age = "" + (index + nextStart + 1);
                        user.job = "实施工程师";
                        user.monthly = "" + 9000 + index + nextStart + 1;
                        dataList.add(user);
                        userStatus = !userStatus;
                    }
                    index++;
                }
                if (nextStart != 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return dataList;
            }

            @Override
            protected void onPostExecute(List<Object> objects) {
                if (getActivity() == null) {
                    return;
                }

                nextStart += size;
                if (adapter == null) {
                    adapter = new AssemblyRecyclerAdapter(objects);

                    headerItemInfo = adapter.addHeaderItem(new HeaderRecyclerItemFactory(), "我是小额头呀！");
                    headerItemInfo2 = adapter.addHeaderItem(new HeaderRecyclerItemFactory(), "唉，我的小额头呢？");
                    adapter.addItemFactory(new UserRecyclerItemFactory(getActivity().getBaseContext()));
                    adapter.addItemFactory(new GameRecyclerItemFactory(getActivity().getBaseContext()));
                    footerItemInfo = adapter.addFooterItem(new HeaderRecyclerItemFactory(), "我是小尾巴呀！");
                    footerItemInfo2 = adapter.addFooterItem(new HeaderRecyclerItemFactory(), "唉，我的小尾巴呢？");
                    adapter.setLoadMoreItem(new LoadMoreRecyclerItemFactory(RecyclerViewFragment.this));

                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.addAll(objects);
                    headerItemInfo2.setEnabled(!headerItemInfo2.isEnabled());
                    footerItemInfo2.setEnabled(!footerItemInfo2.isEnabled());
                }

                boolean loadMoreEnd = nextStart >= 100;
                if (loadMoreEnd) {
                    headerItemInfo.setEnabled(false);
                    footerItemInfo.setEnabled(false);
                }
                adapter.setDisableLoadMore(loadMoreEnd);
            }
        }.execute("");
    }

    @Override
    public void onLoadMore(AssemblyRecyclerAdapter adapter) {
        loadData();
    }
}
