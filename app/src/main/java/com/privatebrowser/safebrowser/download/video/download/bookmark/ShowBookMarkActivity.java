package com.privatebrowser.safebrowser.download.video.download.bookmark;

import static com.privatebrowser.safebrowser.download.video.download.browser.BrowserWindow.browserManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityShowBookMarkBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogAddBookmarkEditBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import com.privatebrowser.safebrowser.download.video.download.browser.BrowserManager;
import com.privatebrowser.safebrowser.download.video.download.browser.BrowserWindow;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.handler.APIManager;
import think.outside.the.box.ui.BaseActivity;

public class ShowBookMarkActivity extends BaseActivity {

    ActivityShowBookMarkBinding bookMarkBinding;
    BookmarksSQLite bookmarksSQLite;
    BrowserManager browserManager;
    private List<BookmarksItem> bookmarks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookMarkBinding = ActivityShowBookMarkBinding.inflate(getLayoutInflater());
        setContentView(bookMarkBinding.getRoot());

        APIManager.showBanner(bookMarkBinding.bannerRelative);


        bookMarkBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        bookMarkBinding.nameText.getPaint().setShader(Utility.setTextGradient(ShowBookMarkActivity.this, bookMarkBinding.nameText));

        bookmarksSQLite = new BookmarksSQLite(ShowBookMarkActivity.this);

        loadBookmarksData();

        bookMarkBinding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(ShowBookMarkActivity.this, R.style.WideDialog);
                DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(ShowBookMarkActivity.this), null, false);
                dialog.setContentView(deleteBinding.getRoot());
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                deleteBinding.titleText.setText(R.string.delete);
                deleteBinding.msgText.setText(R.string.all_data_delete_from_bookmark);

                deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        APIManager.showInter(ShowBookMarkActivity.this, false, isfail -> {
                            bookmarks.clear();
                            bookMarkBinding.bookmarkList.getAdapter().notifyDataSetChanged();
                            if (bookmarks.size() == 0) {
                                bookMarkBinding.bookmarkList.setVisibility(View.GONE);
                                bookMarkBinding.noDataText.setVisibility(View.VISIBLE);
                            } else {
                                bookMarkBinding.bookmarkList.setVisibility(View.VISIBLE);
                                bookMarkBinding.noDataText.setVisibility(View.GONE);
                            }
                            dialog.dismiss();
                        });

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

    class BookmarksItem {
        String type;
        Drawable icon;
        String title;
        String url;
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
        if (bookmarks.size() == 0) {
            bookMarkBinding.bookmarkList.setVisibility(View.GONE);
            bookMarkBinding.noDataText.setVisibility(View.VISIBLE);
        } else {
            bookMarkBinding.bookmarkList.setVisibility(View.VISIBLE);
            bookMarkBinding.noDataText.setVisibility(View.GONE);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShowBookMarkActivity.this, RecyclerView.VERTICAL, false);
        bookMarkBinding.bookmarkList.setLayoutManager(linearLayoutManager);
        bookMarkBinding.bookmarkList.setAdapter(new BookmarksAdapter());
    }


    private class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarkItem> {
        @NonNull
        @Override
        public BookmarkItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ShowBookMarkActivity.this);
            return new BookmarkItem(inflater.inflate(R.layout.item_bookmark, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull BookmarkItem holder, int position) {
            holder.bind(bookmarks.get(position));
        }

        @Override
        public int getItemCount() {
            return bookmarks.size();
        }

        class BookmarkItem extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView icon;
            private TextView title, url;
            private ImageView menu;
            BookmarksItem bookmarksItem;
            Dialog dialog;

            BookmarkItem(View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.bookmarkIcon);
                title = itemView.findViewById(R.id.bookmarkTitle);
                url = itemView.findViewById(R.id.bookmarkurl);
                menu = itemView.findViewById(R.id.bookmarkMenu);
                itemView.setOnClickListener(this);
                menu.setOnClickListener(this);
            }

            void bind(BookmarksItem bookmark) {
                this.bookmarksItem = bookmark;
                icon.setImageDrawable(bookmark.icon);
                title.setText(bookmark.title);
                url.setText(bookmark.url);
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (v == itemView) {
                    switch (bookmarks.get(getAdapterPosition()).type) {
                        case "link":
                            BrowserWindow.newUrlLoad = true;
                            BrowserWindow.url = bookmarks.get(getAdapterPosition()).url;
                            APIManager.showInter(ShowBookMarkActivity.this, false, (AdsCallback) b -> {
                                finish();
                            });
                            break;
                    }
                } else if (v == menu) {
                    PopupMenu bookmarksMenu = new PopupMenu(ShowBookMarkActivity.this, v, Gravity.END);
                    bookmarksMenu.getMenu().add(Menu.NONE, Menu.NONE, 0, "Edit");
                    bookmarksMenu.getMenu().add(Menu.NONE, Menu.NONE, 1, "Delete");

                    bookmarksMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getTitle().toString()) {
                                case "Edit":
                                    final int position;
                                    position = getAdapterPosition() + 1;
                                    dialog = new Dialog(ShowBookMarkActivity.this, R.style.WideDialog);
//                                    DialogAddBookmarkEditBinding addBookmarkBinding = DataBindingUtil.inflate(LayoutInflater.from(ShowBookMarkActivity.this), R.layout.dialog_add_bookmark_edit, null, false);
                                    DialogAddBookmarkEditBinding addBookmarkBinding = DialogAddBookmarkEditBinding.inflate(LayoutInflater.from(ShowBookMarkActivity.this), null, false);
                                    dialog.setContentView(addBookmarkBinding.getRoot());
                                    dialog.setCancelable(false);
                                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    dialog.show();

                                    addBookmarkBinding.editBookmarkurl.setText(bookmarksItem.url);
                                    addBookmarkBinding.addBookmarkTitle.setText(bookmarksItem.title);

                                    addBookmarkBinding.okButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (addBookmarkBinding.addBookmarkTitle.getText().toString().equals("")) {
                                                Toast.makeText(ShowBookMarkActivity.this, getString(R.string.empty_edit_toast), Toast.LENGTH_SHORT).show();
                                            } else if (addBookmarkBinding.editBookmarkurl.getText().toString().equals("")) {
                                                Toast.makeText(ShowBookMarkActivity.this, getString(R.string.empty_edit_url_toast), Toast.LENGTH_SHORT).show();
                                            } else {
                                                String title = addBookmarkBinding.addBookmarkTitle.getText().toString().trim();
                                                String url = addBookmarkBinding.editBookmarkurl.getText().toString().trim();
                                                bookmarksSQLite.editBookmark(position, title, url);
                                                notifyDataSetChanged();
                                                dialog.dismiss();
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

                                    break;

                                case "Delete":
                                    dialog = new Dialog(ShowBookMarkActivity.this, R.style.WideDialog);
                                    DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(ShowBookMarkActivity.this), null, false);
                                    dialog.setContentView(deleteBinding.getRoot());
                                    dialog.setCancelable(false);
                                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    dialog.show();

                                    deleteBinding.titleText.setText(R.string.Delete_bookmark);
                                    deleteBinding.msgText.setText(R.string.this_bookmark);

                                    deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            bookmarksSQLite.deleteBookMark((getAdapterPosition() + 1));
                                            Toast.makeText(ShowBookMarkActivity.this, "Delete from Bookmark.", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            notifyDataSetChanged();
                                            loadBookmarksData();
                                        }
                                    });

                                    deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                        }
                                    });

                                    break;
                            }
                            return true;
                        }
                    });
                    bookmarksMenu.show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        APIManager.showInter(ShowBookMarkActivity.this, true, (AdsCallback) b -> {
            finish();
        });
    }
}