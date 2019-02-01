package ru.tcgeo.application.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.DirCallback;

public class DirHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.llItem)
    View llItem;

    @BindView(R.id.vSpace)
    View vSpace;

    @BindView(R.id.ivIcon)
    ImageView ivIcon;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.ivOpen)
    ImageView ivOpen;

    DirCallback callback;

    public DirHolder(View itemView, DirCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.callback = callback;
    }

    public void bind(File file, int position) {
        if (position == 0) {
            vSpace.setVisibility(View.GONE);
            ivIcon.setImageResource(R.drawable.ic_parent_folder);
            ivOpen.setVisibility(View.INVISIBLE);
            tvName.setText(file.getAbsolutePath());
            llItem.setOnClickListener(v -> callback.onExit(this));

        } else if (file.isDirectory()) {
            ivIcon.setImageResource(R.drawable.ic_folder);
            ivOpen.setVisibility(View.VISIBLE);
            tvName.setText(file.getName());
            llItem.setOnClickListener(v -> callback.onEnter(this));
        } else {
            ivIcon.setImageResource(R.drawable.ic_file);
            ivOpen.setVisibility(View.VISIBLE);
            tvName.setText(file.getName());
        }
        ivOpen.setOnClickListener(v -> callback.onOpen(this));

    }
}
