package net.esang.mlinkup.dreamsecurity.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamsecurity.magicxsign.model.Certificate;
import com.dreamsecurity.magicxsign.model.CertificateDetail;

import net.esang.mlinkup.databinding.ItemCertificateBinding;

public class CertificateListAdapter extends ListAdapter<Certificate, CertificateListAdapter.ItemCertificateViewHolder> {

    private int selectedPosition = RecyclerView.NO_POSITION;
    private Callback callback;

    public interface Callback {
        void onItemClicked(Certificate certificate);
    }

    public CertificateListAdapter(Callback callback) {
        super(DIFF_UTIL);
        this.callback = callback;
    }

    public static final DiffUtil.ItemCallback<Certificate> DIFF_UTIL = new DiffUtil.ItemCallback<Certificate>() {
        @Override
        public boolean areItemsTheSame(@NonNull Certificate oldItem, @NonNull Certificate newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Certificate oldItem, @NonNull Certificate newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ItemCertificateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate ItemCertificateBinding and return the ViewHolder
        ItemCertificateBinding itemCertificateBinding = ItemCertificateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemCertificateViewHolder(itemCertificateBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCertificateViewHolder holder, int position) {
        holder.bind(position);
    }

    // Reset selected position
    public void resetSelectedPosition() {
        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    public class ItemCertificateViewHolder extends RecyclerView.ViewHolder {
        ItemCertificateBinding binding;

        public ItemCertificateViewHolder(ItemCertificateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            Certificate currentItem = getCurrentList().get(position);
            CertificateDetail signDetail = currentItem.getSignDetail();

            // Bind data to views
            binding.tvRealName.setText(signDetail.getRealName());
            binding.tvSubjectDN.setText(signDetail.getSubjectDN());
            binding.tvExpire.setText(signDetail.getExpirationFrom() + "~" + signDetail.getExpirationTo());

// ⭐ selector 적용 핵심
            binding.getRoot().setSelected(position == selectedPosition);

            binding.getRoot().setOnClickListener(v -> {
                int prevPosition = selectedPosition;

                // 토글 처리
                if (selectedPosition == position) {
                    selectedPosition = RecyclerView.NO_POSITION;
                    callback.onItemClicked(null);
                } else {
                    selectedPosition = position;
                    callback.onItemClicked(currentItem);
                }

                // 🔥 필요한 아이템만 갱신
                if (prevPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(prevPosition);
                }
                notifyItemChanged(position);
            });
//            // Highlight the selected item
//            if (position == selectedPosition) {
//                binding.getRoot().setBackgroundColor(Color.GRAY);
//            } else {
//                binding.getRoot().setBackgroundColor(Color.WHITE);
//            }
//
//            // Handle item click
//            binding.getRoot().setOnClickListener(v -> {
//                if (selectedPosition == position) {
//                    selectedPosition = RecyclerView.NO_POSITION;
//                } else {
//                    selectedPosition = position;
//                }
//
//                // Trigger the callback with the selected item or null if deselected
//                if (selectedPosition != RecyclerView.NO_POSITION) {
//                    callback.onItemClicked(currentItem);
//                } else {
//                    callback.onItemClicked(null);
//                }
//
//                notifyDataSetChanged();
//            });
        }
    }
}

