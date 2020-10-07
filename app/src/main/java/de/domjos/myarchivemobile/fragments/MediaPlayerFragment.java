/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2020 Dominic Joas.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.myarchivemobile.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.utils.IntentHelper;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.helper.PDFReaderHelper;

public class MediaPlayerFragment extends AbstractFragment<BaseMediaObject> {
    private Type type;
    private String path;
    private BaseMediaObject baseMediaObject;

    private ImageButton cmdZoomMinus;
    private ImageButton cmdZoomPlus;
    private TextView lblZoom, lblCurrent;
    private ImageView ivCurrent;
    private VideoView vvCurrent;

    private int current, max;
    private float mScaleFactor = 1.0f;
    private PDFReaderHelper pdfReaderHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_player, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.cmdZoomMinus = view.findViewById(R.id.cmdPdfZoomMinus);
        this.cmdZoomPlus = view.findViewById(R.id.cmdPdfZoomPlus);
        ImageButton cmdNext = view.findViewById(R.id.cmdNext);
        ImageButton cmdStop = view.findViewById(R.id.cmdStop);
        ImageButton cmdPlay = view.findViewById(R.id.cmdPlay);
        ImageButton cmdPrevious = view.findViewById(R.id.cmdPrevious);
        this.lblZoom = view.findViewById(R.id.lblPdfZoom);
        this.lblCurrent = view.findViewById(R.id.lblCurrent);

        this.ivCurrent = view.findViewById(R.id.ivCurrent);
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this.getActivity(), new ScaleListener());
        this.ivCurrent.setOnTouchListener((view1, motionEvent) -> {
            scaleGestureDetector.onTouchEvent(motionEvent);
            return false;
        });

        this.vvCurrent = view.findViewById(R.id.vvCurrent);
        this.vvCurrent.setOnPreparedListener(mediaPlayer -> {
            this.updateDurationLabel();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    MediaPlayerFragment.this.requireActivity().runOnUiThread(()->updateDurationLabel());
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 1000, 1000);
        });

        cmdNext.setOnClickListener(event -> {
            if(this.type == Type.Book) {
                if(this.current < (this.max-1)) {
                    this.openPDF(++this.current);
                }
            } else {
                if(this.vvCurrent.getCurrentPosition() + 10000 < this.vvCurrent.getDuration()) {
                    this.vvCurrent.seekTo(this.vvCurrent.getCurrentPosition() + 10000);
                }
            }
        });

        cmdPrevious.setOnClickListener(event -> {
            if(this.type == Type.Book) {
                if (this.current > 0) {
                    this.openPDF(--this.current);
                }
            } else {
                if (this.vvCurrent.getCurrentPosition() > 10000) {
                    this.vvCurrent.seekTo(this.vvCurrent.getCurrentPosition() - 10000);
                }
            }
        });

        cmdPlay.setOnClickListener(event -> {
            if(this.type == Type.Book) {
                this.openPDF(0);
            } else {
                if(this.path.toLowerCase().endsWith("mp4") || this.path.toLowerCase().endsWith("avi")) {
                    this.vvCurrent.setVideoPath(this.path);
                    this.vvCurrent.start();
                } else {
                    if(this.path.toLowerCase().contains("youtu")) {
                        if(!IntentHelper.startYoutubeIntent(this.path, this.requireActivity())) {
                            IntentHelper.startBrowserIntent(this.path, this.requireActivity());
                        }
                    } else {
                        IntentHelper.startBrowserIntent(this.path, this.requireActivity());
                    }
                }
            }
        });

        cmdStop.setOnClickListener(event -> {
            if(this.type == Type.Book) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if(this.pdfReaderHelper != null) {
                        this.pdfReaderHelper.close();
                    }
                }
            } else {
                if(this.vvCurrent.isPlaying()) {
                    this.vvCurrent.stopPlayback();
                }
            }
        });

        this.cmdZoomPlus.setOnClickListener(event -> this.scale((this.mScaleFactor-0.1f)));
        this.cmdZoomMinus.setOnClickListener(event -> this.scale((this.mScaleFactor+0.1f)));
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.baseMediaObject = baseMediaObject;

        if(this.baseMediaObject instanceof Book) {
            this.type = Type.Book;
            this.path = ((Book) baseMediaObject).getPath();
            this.cmdZoomMinus.setVisibility(View.VISIBLE);
            this.cmdZoomPlus.setVisibility(View.VISIBLE);
            this.ivCurrent.setVisibility(View.VISIBLE);
            this.vvCurrent.setVisibility(View.GONE);
        } else if(this.baseMediaObject instanceof Movie) {
            this.type = Type.Movie;
            this.path = ((Movie) baseMediaObject).getPath();
            this.cmdZoomMinus.setVisibility(View.INVISIBLE);
            this.cmdZoomPlus.setVisibility(View.INVISIBLE);
            this.ivCurrent.setVisibility(View.GONE);
            this.vvCurrent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public BaseMediaObject getMediaObject() {
        if(this.type == Type.Book) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                this.pdfReaderHelper.close();
            }
        }
        return this.baseMediaObject;
    }

    private void updateDurationLabel() {
        float duration = this.vvCurrent.getDuration() / 1000.0f;
        float position = this.vvCurrent.getCurrentPosition() / 1000.0f;
        String content = String.format("%ss / %ss", Math.round(position), Math.round(duration));
        this.lblCurrent.setText(content);
    }

    @Override
    public void changeMode(boolean editMode) {}

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }

    private void openPDF(int page) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.pdfReaderHelper = new PDFReaderHelper(this.path);
                this.pdfReaderHelper.openPage(page);
                this.current = this.pdfReaderHelper.getCurrentPageNumber();
                this.max = this.pdfReaderHelper.getPagesCount();
                String content = this.current + 1 + " / " + this.max;
                this.lblCurrent.setText(content);
                this.ivCurrent.setImageBitmap(this.pdfReaderHelper.getPage());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.ivCurrent.setBackgroundColor(this.getResources().getColor(android.R.color.white, this.requireContext().getTheme()));
                } else {
                    this.ivCurrent.setBackgroundColor(this.requireContext().getResources().getColor(android.R.color.white));
                }

                this.mScaleFactor = 1.0f;
                this.scale(this.mScaleFactor);
            } else {
                IntentHelper.startPDFIntent(this.requireActivity(), this.path);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    public enum Type {
        Book,
        Movie
    }

    private void scale(float scale) {
        mScaleFactor = Math.max(0.1f, Math.min(scale, 10.0f));
        ivCurrent.setScaleX(mScaleFactor);
        ivCurrent.setScaleY(mScaleFactor);
        String zoomContent =  this.mScaleFactor * 100 + "%";
        this.lblZoom.setText(zoomContent);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            scale(mScaleFactor);
            return true;
        }
    }
}