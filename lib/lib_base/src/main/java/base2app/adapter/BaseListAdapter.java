package base2app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import kotlin.Deprecated;
import kotlinx.android.extensions.LayoutContainer;

/**
 * 基础的RecyclerView的Adapter
 */
public abstract class BaseListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected String TAG;
    protected List<T> items;
    protected OnClickItemCallback<T> clickItemCallback;
    protected onLongClickItemCallback<T> longClickItemCallback;

    public BaseListAdapter() {
        TAG = this.getClass().getName();
        items = new ArrayList<>();
    }

    public List<? extends T> getItems() {
        return items;
    }

    public void setItems(List<? extends T> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        if (item == null) return;
        this.items.add(item);
        notifyDataSetChanged();
    }

    public void addItem(int index, T item) {
        if (item == null) return;
        this.items.add(index, item);
        notifyDataSetChanged();
    }

    public void insertedItem(int index, T item) {
        if (item == null) return;
        this.items.add(index, item);
    }

    public void insertedItem(T item) {
        if (item == null) return;
        this.items.add(item);
    }

    public void addItems(List<? extends T> items) {
        if (items == null) return;
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void addItems(int index, List<? extends T> items) {
        if (items == null) return;
        this.items.addAll(index, items);
        notifyDataSetChanged();
    }

    public void clearItems() {
        this.items.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取数据
     *
     * @param index 数据集合中的位置
     */
    @androidx.annotation.Nullable
    public final T getItem(int index) {
        return index < 0 || index >= items.size() ? null : items.get(index);
    }

    /**
     * 获取item在数据集合位置
     *
     * @param position adapter中的位置
     */
    public int getItemIndex(int position) {
        return position;
    }


    /**
     * 设置item点击事件，viewHolder需要实现IListItemViewHolderClick
     */
    public void setClickItemCallback(OnClickItemCallback<T> clickItemCallback) {
        this.clickItemCallback = clickItemCallback;
    }

    /**
     * 设置item长按事件，viewHolder需要实现IListItemViewHolderClick
     */
    public void setLongClickItemCallback(onLongClickItemCallback<T> longClickItemCallback) {
        this.longClickItemCallback = longClickItemCallback;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof IListItemViewHolder) {
            int index = getItemIndex(position);
            ((IListItemViewHolder) holder).bind(getItem(index), index);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && holder instanceof IListItemViewHolder) {
            int index = getItemIndex(position);
            T t = getItem(index);
            IListItemViewHolder iItem = (IListItemViewHolder) holder;
            if (!iItem.bindViewPart(t, index, payloads)){
                iItem.bind(t,index);
            }
        }else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    /**
     * 子类要实现该方法请在viewHolder中实现IListItemViewHolder2
     */
    @Override
    public final void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof IListItemViewHolder2) {
            ((IListItemViewHolder2) holder).attached();
        }
    }

    /**
     * 子类要实现该方法请在viewHolder中实现IListItemViewHolder2
     */
    @Override
    public final void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof IListItemViewHolder2) {
            ((IListItemViewHolder2) holder).detached();
        }
    }

    /**
     * 子类要实现该方法请在viewHolder中实现IListItemViewHolder2
     */
    @Override
    public final void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof IListItemViewHolder2) {
            ((IListItemViewHolder2) holder).recycled();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = createViewHolderByParent(parent, viewType);
        if (viewHolder instanceof IListItemViewHolderClick) {
            IListItemViewHolderClick listHolder = (IListItemViewHolderClick) viewHolder;
            if (listHolder.isNeedClick()) setItemClickListener(viewHolder);
            if (listHolder.isNeedLongClick()) setLongClickListener(viewHolder);
        }
        return viewHolder;
    }


    /**
     * 设置长按事件
     */
    protected void setLongClickListener(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setOnLongClickListener(v1 -> {
            //在RecyclerView中的绝对位置
            int absolutePosition = viewHolder.getAbsoluteAdapterPosition();
            //在adapter的位置
            int bindingPosition = viewHolder.getBindingAdapterPosition();
            //在数据集合的位置
            int itemIndex = getItemIndex(bindingPosition);
            T item = getItem(itemIndex);
            onLongClickItem(absolutePosition, bindingPosition, itemIndex, item, v1);
            if (longClickItemCallback != null)
                longClickItemCallback.onLongClickItem(itemIndex, item, v1);
            return true;
        });
    }

    /**
     * 设置点击事件
     */
    protected void setItemClickListener(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setOnClickListener(v1 -> {
            //在RecyclerView中的绝对位置
            int absolutePosition = viewHolder.getAbsoluteAdapterPosition();
            //在adapter的位置
            int bindingPosition = viewHolder.getBindingAdapterPosition();
            //数据在数据集合的位置
            int itemIndex = getItemIndex(bindingPosition);
            if (itemIndex < 0) return;
            T item = getItem(itemIndex);
            onClickItem(absolutePosition, bindingPosition, itemIndex, item, v1);
            if (clickItemCallback != null) clickItemCallback.onClickItem(itemIndex, item, v1);
        });
    }


    /**
     * 点击
     *
     * @param absolutePsition 在RecyclerView中的位置
     * @param bindingPosition 在bindingAdapter位置
     * @param itemIndex       在数据集合的位置
     * @param item            数据
     * @param itemView        view
     */
    protected void onClickItem(int absolutePsition, int bindingPosition, int itemIndex, T item, View itemView) {

    }

    /**
     * 长按
     *
     * @param absolutePsition 在RecyclerView中的位置
     * @param bindingPosition 在bindingAdapter位置
     * @param itemIndex       在数据集合的位置
     * @param item            数据
     * @param itemView        view
     */
    protected void onLongClickItem(int absolutePsition, int bindingPosition, int itemIndex, T item, View itemView) {

    }

    /**
     * 子类创建ViewHolder,itemView推荐使用[BaseListAdapter.inflateView()]方法创建
     */
    @NotNull
    protected RecyclerView.ViewHolder createViewHolderByParent(@NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        int layoutId = getLayout(viewType);
        if (layoutId != 0){
            View v = inflateView(parent,getLayout(viewType));
            holder = createViewHolder(v, viewType);
        }
        if (holder == null) {
            holder = new RecyclerView.ViewHolder(new FrameLayout(parent.getContext())) {
            };
        }
        return holder;
    }

    protected View inflateView(ViewGroup parent,int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    @Deprecated(message = "use createViewHolderByParent")
    protected int getLayout(int viewType) {
        return 0;
    }

    /**
     * @deprecated use createViewHolderByParent
     */
    @Deprecated(message = "use createViewHolderByParent")
    protected RecyclerView.ViewHolder createViewHolder(View itemView, int viewType) {
        return null;
    }

    /**
     * 基础的ViewHolder实现
     * 默认实现单击；长按
     *
     * @param <T>
     */
    public abstract static class ListItemViewHolder<T> extends RecyclerView.ViewHolder implements IListItemViewHolderClick, IListItemViewHolder<T> {

        @Nullable
        @Override
        public View getContainerView() {
            return itemView;
        }

        public boolean needClick;
        public boolean needLongClick;

        public ListItemViewHolder(View v, boolean needClick, boolean needLongClick) {
            super(v);
            this.needClick = needClick;
            this.needLongClick = needLongClick;
        }

        @Override
        public boolean isNeedClick() {
            return needClick;
        }

        @Override
        public boolean isNeedLongClick() {
            return needLongClick;
        }

        @Override
        public boolean bindViewPart(T item, int index) {
            return false;
        }


    }

    public interface OnClickItemCallback<T> {
        /**
         * 列表点击事件
         *
         * @param itemIndex 在数据集合中的位置
         */
        void onClickItem(int itemIndex, T item, View v);
    }

    public interface onLongClickItemCallback<T> {
        /**
         * 列表长按事件
         *
         * @param itemIndex 在数据集合中的位置
         */
        void onLongClickItem(int itemIndex, T item, View view);
    }

    public interface IListItemViewHolder<T> extends LayoutContainer {

        /**
         * @param item      数据
         * @param itemIndex 在数据集合中的位置
         */
        void bind(T item, int itemIndex);

        /**
         * 局部绑定更新;
         * 具体局部更新的区域由编码决定
         *
         * @param item      数据
         * @param itemIndex 在数据集合中的位置
         * @deprecated 使用bindViewPart(item,itenindex,payloads)
         */
        @java.lang.Deprecated
        default boolean bindViewPart(T item, int itemIndex){
            return false;
        }

        /**
         * 局部绑定更新;
         * 具体局部更新的区域由编码决定
         *
         * @param item      数据
         * @param itemIndex 在数据集合中的位置
         */
        default boolean bindViewPart(T item, int itemIndex,@NonNull List<Object> payloads){
            return bindViewPart(item,itemIndex);
        }
    }

    public interface IListItemViewHolder2 {

        /**
         * item添加到界面
         */
        default void attached(){

        }

        /**
         * item从界面移除
         */
        default void detached(){
        }

        /**
         * item复用回收
         */
        default void recycled(){
        }
    }

    public interface IListItemViewHolderClick {

        boolean isNeedClick();

        boolean isNeedLongClick();
    }
}
