package com.zhuorui.commonwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by Poker on 2016/2/29.
 */
public class ImageListView extends HorizontalScrollView {
    private LinearLayout rootView;

    private int imgHeight;
    private int imgWidth;
    private int margin;
    private List<CharSequence> images;

    public ImageListView(Context context) {
        super(context);
        initWidget(null);
    }

    public ImageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWidget(attrs);
    }

    private void initWidget(AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;
        imgHeight = (int) (density * 68f);
        imgWidth = (int) (density * 88f);
        margin = (int) (density * 10);
        initAttributes(attrs);
        initView();
    }

    private void initAttributes(AttributeSet attrs) {
        if (null != attrs) {

        }
    }

    private void initView() {
        View v = View.inflate(getContext(), R.layout.view_image_list, this);
        rootView = (LinearLayout) v.findViewById(R.id.rootView);
    }

    public void setDatas(List<CharSequence> images) {
        this.images = images;
        rootView.removeAllViews();
        if (null != images) {
            for (int i = 0, len = images.size(); i < len; i++) {
                rootView.addView(createImageView(i, images.get(i).toString()));
            }
        }
    }

    private View createImageView(int position, String img) {
//        SimpleDraweeView sdv = new SimpleDraweeView(getContext());
//        GenericDraweeHierarchy hierarchy= GenericDraweeHierarchyBuilder.newInstance(getResources()).setRoundingParams(RoundingParams.fromCornersRadius(5)).build();
//        sdv.setHierarchy(hierarchy);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imgWidth, imgHeight);
//        layoutParams.setMargins(0, margin, margin, margin);
//        sdv.setLayoutParams(layoutParams);
//        sdv.setImageURI(Uri.parse(img + ""));
//        sdv.setTag(position);
//        sdv.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Integer position = (Integer) v.getTag();
//                if (null != position) {
//                    //跳转图片浏览页
//                    ImgViewerActivity.startActivity(v.getContext(), new ExhibitList(images, position));
//                }
//            }
//        });
        ImageView sdv = new ImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imgWidth, imgHeight);
        layoutParams.setMargins(0, margin, margin, margin);
        sdv.setLayoutParams(layoutParams);
        return sdv;
    }

}
