package com.privatebrowser.safebrowser.download.video.download.history;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import com.privatebrowser.safebrowser.download.video.download.browser.BrowsingActivity;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.handler.APIManager;

public class VisitedPagesAdapterFragment extends RecyclerView.Adapter<VisitedPagesAdapterFragment.VisitedPageItem> {

    Context context;
    List<VisitedPage> visitedPages;
    private HistorySQLite historySQLite;

    public VisitedPagesAdapterFragment(Context context, List<VisitedPage> visitedPages) {
        this.context = context;
        this.visitedPages = visitedPages;
        historySQLite = new HistorySQLite(context);
    }

    @NonNull
    @Override
    public VisitedPageItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new VisitedPageItem(inflater.inflate(R.layout.item_history_fragment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VisitedPageItem holder, int position) {
        holder.bind(visitedPages.get(position));
    }

    @Override
    public int getItemCount() {
        return visitedPages.size();
    }

    class VisitedPageItem extends RecyclerView.ViewHolder {
        private TextView title,url;

        VisitedPageItem(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.visitedPageTitle);
            url = itemView.findViewById(R.id.visitedPageurl);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BrowsingActivity.class);
                    intent.setData(Uri.parse(visitedPages.get(getAdapterPosition()).link));
                    APIManager.showInter((Activity) context, false, (AdsCallback) b -> {
                        context.startActivity(intent);
                    });
                }
            });
            itemView.findViewById(R.id.visitedPageDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog dialog = new Dialog(context, R.style.WideDialog);
                    DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(context),null, false);
                    dialog.setContentView(deleteBinding.getRoot());
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                    deleteBinding.titleText.setText(R.string.delete);
                    deleteBinding.msgText.setText(R.string.delete_this_data_from_history);

                    deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            historySQLite.deleteFromHistory(visitedPages.get(getAdapterPosition()).link);
                            visitedPages.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            dialog.dismiss();
                        }
                    });

                    deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                }
            });
        }

        void bind(VisitedPage page) {
            title.setText(page.title);
            url.setText(page.link);
        }
    }
}