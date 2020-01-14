package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.BookPagerAdapter;


public class MainBooksFragment extends Fragment {
    private SwipeRefreshDeleteList lvBooks;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_books, container, false);

        this.lvBooks = root.findViewById(R.id.lvMediaBooks);
        this.tabLayout = root.findViewById(R.id.tabLayout);
        this.viewPager = root.findViewById(R.id.viewPager);
        this.tabLayout.setupWithViewPager(this.viewPager);

        BookPagerAdapter bookPagerAdapter = new BookPagerAdapter(Objects.requireNonNull(this.getFragmentManager()), this.getContext());
        this.viewPager.setAdapter(bookPagerAdapter);

        Objects.requireNonNull(this.tabLayout.getTabAt(0)).setIcon(R.drawable.ic_book_black_24dp);
        Objects.requireNonNull(this.tabLayout.getTabAt(1)).setIcon(R.drawable.ic_image_black_24dp);
        Objects.requireNonNull(this.tabLayout.getTabAt(2)).setIcon(R.drawable.ic_book_black_24dp);
        Objects.requireNonNull(this.tabLayout.getTabAt(3)).setIcon(R.drawable.ic_book_black_24dp);
        Objects.requireNonNull(this.tabLayout.getTabAt(4)).setIcon(R.drawable.ic_person_black_24dp);

        return root;
    }
}