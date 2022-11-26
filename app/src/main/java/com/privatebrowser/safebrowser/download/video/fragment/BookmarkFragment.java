package com.privatebrowser.safebrowser.download.video.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.DialogAddBookmarkEditBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import com.privatebrowser.safebrowser.download.video.databinding.FragmentBookmarkBinding;
import com.privatebrowser.safebrowser.download.video.download.bookmark.BookmarksSQLite;
import com.privatebrowser.safebrowser.download.video.download.browser.BrowsingActivity;
import com.privatebrowser.safebrowser.download.video.utils.Converters;
import com.privatebrowser.safebrowser.download.video.utils.Utility;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.handler.APIManager;

public class BookmarkFragment extends Fragment {
    Intent intent;
    FragmentBookmarkBinding binding;
    BookmarksSQLite bookmarksSQLite;
    private List<BookmarksItem> bookmarks;

    class BookmarksItem {
        String type;
        Drawable icon;
        String title;
        String url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBookmarkBinding.inflate(inflater,container,false);
        bookmarksSQLite = new BookmarksSQLite(getActivity());
        loadBookmarksData();
        return binding.getRoot();
    }

    @SuppressLint("Range")
    private void loadBookmarksData() {
        bookmarks = new ArrayList<>();
        if (!bookmarksSQLite.getCurrentTable().equals("bookmarks")) {
            BookmarksItem b = new BookmarksItem();
            b.type = "upFolder";
            b.icon = getResources().getDrawable(R.drawable.google_icon);
            b.title = "...";
            bookmarks.add(b);
        }
        Cursor cursor = bookmarksSQLite.getBookmarks();
        while (cursor.moveToNext()) {
            BookmarksItem b = new BookmarksItem();
            b.type = cursor.getString(cursor.getColumnIndex("type"));
            b.title = cursor.getString(cursor.getColumnIndex("title"));
            if (b.type.equals("folder")) {
                b.icon = getResources().getDrawable(R.drawable.google_icon);
            } else {
                byte[] iconInBytes = cursor.getBlob(cursor.getColumnIndex("icon"));
                if (iconInBytes != null) {
                    Bitmap iconBitmap = BitmapFactory.decodeByteArray(iconInBytes, 0, iconInBytes.length);
//                    Log.e("Bitmap ", "loadBookmarksData: " + iconBitmap.getWidth() + "  " + iconBitmap.getHeight());
                    b.icon = new BitmapDrawable(getResources(), iconBitmap);
                } else {
                    b.icon = getResources().getDrawable(R.drawable.google_icon);
                }
                b.url = cursor.getString(cursor.getColumnIndex("link"));
            }
            bookmarks.add(b);
        }
        cursor.close();
        Collections.reverse(bookmarks);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        binding.bookmarkList.setLayoutManager(gridLayoutManager);
        binding.bookmarkList.setAdapter(new BookmarksAdapter());
    }

    private class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarkItem> {

        boolean longclick = false;

        @NonNull
        @Override
        public BookmarkItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new BookmarkItem(inflater.inflate(R.layout.myitem_book_mark, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull BookmarkItem holder, @SuppressLint("RecyclerView") int position) {
            if (position == 0) {
                holder.icon.setImageResource(R.drawable.ic_add_bookmark);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity(), R.style.WideDialog);
//                        DialogAddBookmarkEditBinding addBookmarkBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_add_bookmark_edit, null, false);

                        DialogAddBookmarkEditBinding addBookmarkBinding = DialogAddBookmarkEditBinding.inflate(LayoutInflater.from(getActivity()), null, false);
                        dialog.setContentView(addBookmarkBinding.getRoot());
                        dialog.setCancelable(false);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();

                        addBookmarkBinding.okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (addBookmarkBinding.addBookmarkTitle.getText().toString().equals("")) {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.empty_edit_toast), Toast.LENGTH_SHORT).show();
                                } else if (addBookmarkBinding.editBookmarkurl.getText().toString().equals("")) {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.empty_edit_url_toast), Toast.LENGTH_SHORT).show();
                                } else {
                                    String title = addBookmarkBinding.addBookmarkTitle.getText().toString().trim();
                                    String url = addBookmarkBinding.editBookmarkurl.getText().toString().trim();
                                    byte[] bytes;
                                    bytes = Converters.getImageFromResource(getActivity(), R.drawable.globe_icon);
                                    bookmarksSQLite.add(bytes, title, url);
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Page saved into bookmarks", Toast.LENGTH_SHORT).show();
                                    loadBookmarksData();
                                }
                            }
                        });
                        addBookmarkBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
            } else {
                if (longclick) {
                    if (position != 0)
                        holder.close.setVisibility(View.VISIBLE);
                } else {
                    holder.close.setVisibility(View.GONE);
                }

                holder.bind(bookmarks.get(position - 1));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setIntentActivity(bookmarks.get(position - 1).url);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        longclick = !longclick;
                        notifyDataSetChanged();
                        return true;
                    }
                });

                holder.close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity(), R.style.WideDialog);
//                        DialogDeleteBinding deleteBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_delete, null, false);
                        DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(getActivity()), null, false);
                        dialog.setContentView(deleteBinding.getRoot());
                        dialog.setCancelable(false);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();

                        deleteBinding.titleText.setText(R.string.delete_bookmark);
                        deleteBinding.msgText.setText(R.string.this_bookmark_2);

                        deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bookmarksSQLite.deleteBookmark(bookmarks.get(position - 1).url);
                                longclick = !longclick;
                                notifyDataSetChanged();
                                loadBookmarksData();
                                Toast.makeText(getActivity(), "Delete from Bookmark.", Toast.LENGTH_SHORT).show();
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
        }

        @Override
        public int getItemCount() {
            return bookmarks.size() + 1;
        }

        class BookmarkItem extends RecyclerView.ViewHolder {
            private ImageView icon;
            private ImageView close;
            private MaterialTextView title;

            BookmarkItem(View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.iconButton);
                close = itemView.findViewById(R.id.closeButton);
                title = itemView.findViewById(R.id.title);
            }

            void bind(BookmarksItem bookmark) {
                icon.setImageDrawable(bookmark.icon);
                title.setText(bookmark.title);
                title.setSingleLine(true);
                title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                title.setSelected(true);
            }

        }
    }

    public void setIntentActivity(String data) {
        if (Utility.isNetworkConnected(getActivity())) {

            intent = new Intent(getActivity(), BrowsingActivity.class);
            intent.setData(Uri.parse(data));
            APIManager.showInter(getActivity(), false, (AdsCallback) b -> {
                startActivity(intent);
            });
        } else {
            Toast.makeText(getActivity(), "No Internet Connection, Please Try Again..", Toast.LENGTH_SHORT).show();
        }
    }

}