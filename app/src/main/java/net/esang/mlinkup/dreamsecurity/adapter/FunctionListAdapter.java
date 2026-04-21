package net.esang.mlinkup.dreamsecurity.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import net.esang.mlinkup.databinding.ItemFunctionBinding;
import net.esang.mlinkup.dreamsecurity.model.ItemFunction;


public class FunctionListAdapter extends ListAdapter<ItemFunction, FunctionListAdapter.ItemFunctionViewHolder> {

    private final Callback callback;

    // 콜백 인터페이스 정의
    public interface Callback {
        void onItemClicked(ItemFunction itemFunction);
    }

    // 생성자
    public FunctionListAdapter(Callback callback) {
        super(diffUtil);
        this.callback = callback;
    }

    @NonNull
    @Override
    public ItemFunctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFunctionBinding itemFunctionBinding = ItemFunctionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemFunctionViewHolder(itemFunctionBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemFunctionViewHolder holder, int position) {
        ItemFunction itemFunction = getItem(position);
        holder.bind(itemFunction);
    }

    // ViewHolder 클래스
    public class ItemFunctionViewHolder extends RecyclerView.ViewHolder {
        private final ItemFunctionBinding itemFunctionBinding;

        public ItemFunctionViewHolder(ItemFunctionBinding itemFunctionBinding) {
            super(itemFunctionBinding.getRoot());
            this.itemFunctionBinding = itemFunctionBinding;
        }

        public void bind(final ItemFunction itemFunction) {
            itemFunctionBinding.tvFunctionTitle.setText(itemFunction.getTitle());
            itemFunctionBinding.tvFunctionDescription.setText(itemFunction.getDescription());
            itemFunctionBinding.getRoot().setOnClickListener(v -> {
                if (callback != null) {
                    callback.onItemClicked(itemFunction);
                }
            });
        }
    }

    // DiffUtil 콜백 정의
    private static final DiffUtil.ItemCallback<ItemFunction> diffUtil = new DiffUtil.ItemCallback<ItemFunction>() {
        @Override
        public boolean areItemsTheSame(@NonNull ItemFunction oldItem, @NonNull ItemFunction newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ItemFunction oldItem, @NonNull ItemFunction newItem) {
            return oldItem.equals(newItem);
        }
    };

}
