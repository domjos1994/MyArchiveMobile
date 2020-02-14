package de.domjos.myarchivemobile.activities;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.CompanyPagerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public final class CompanyActivity extends AbstractActivity {
    private SwipeRefreshDeleteList lvCompanies;
    private ViewPager viewPager;
    private CompanyPagerAdapter companyPagerAdapter;
    private BottomNavigationView bottomNavigationView;

    private Company company = null;
    private Validator validator;

    public CompanyActivity() {
        super(R.layout.company_activity);
    }


    @Override
    protected void initActions() {
        this.lvCompanies.setOnReloadListener(CompanyActivity.this::reload);
        this.lvCompanies.setOnDeleteListener(listObject -> {
            Company company = (Company) listObject.getObject();
            MainActivity.GLOBALS.getDatabase().deleteItem(company);
            this.changeMode(false, false);
            this.companyPagerAdapter.setMediaObject(new Company());
        });
        this.lvCompanies.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.company = (Company) listObject.getObject();
            this.companyPagerAdapter.setMediaObject(this.company);
            this.changeMode(false, true);
        });


        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    this.changeMode(true, false);
                    this.companyPagerAdapter.setMediaObject(new Company());
                    this.company = null;
                    break;
                case R.id.cmdEdit:
                    if(this.company != null) {
                        this.changeMode(true, true);
                        this.companyPagerAdapter.setMediaObject(this.company);
                    }
                    break;
                case R.id.cmdCancel:
                    this.changeMode(false, false);
                    this.company = null;
                    this.reload();
                    break;
                case R.id.cmdSave:
                    if(this.validator.getState()) {
                        Company company = this.companyPagerAdapter.getMediaObject();
                        if(this.company !=null) {
                            company.setId(this.company.getId());
                        }
                        if(this.validator.checkDuplicatedEntry(company.getTitle(), company.getId(), this.lvCompanies.getAdapter().getList())) {
                            MainActivity.GLOBALS.getDatabase().insertOrUpdateCompany(company, "", 0);
                            this.changeMode(false, false);
                            this.company = null;
                            this.reload();
                        }
                    } else {
                        MessageHelper.printMessage(this.validator.getResult(), R.mipmap.ic_launcher_round, CompanyActivity.this);
                    }
                    break;
            }
            return true;
        });
    }

    @Override
    protected void initControls() {
        this.lvCompanies = this.findViewById(R.id.lvCompanies);
        this.bottomNavigationView = this.findViewById(R.id.navigationView);

        TabLayout tabLayout = this.findViewById(R.id.tabLayout);
        this.viewPager = this.findViewById(R.id.viewPager);
        this.viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(this.viewPager);

        this.companyPagerAdapter = new CompanyPagerAdapter(this.getSupportFragmentManager(), getApplicationContext());
        this.validator = this.companyPagerAdapter.initValidator();
        this.viewPager.setAdapter(this.companyPagerAdapter);

        for(int i = 0; i<=tabLayout.getTabCount()-1; i++) {
            tabLayout.setScrollPosition(i, 0f, true);
            this.viewPager.setCurrentItem(i);
        }
        tabLayout.setScrollPosition(0, 0f, true);
        this.viewPager.setCurrentItem(0);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_people_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_image_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_format_list_bulleted_black_24dp);

        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(false);
    }

    @Override
    protected void reload() {
        try {
            this.lvCompanies.getAdapter().clear();
            for(Company company : MainActivity.GLOBALS.getDatabase().getCompanies("", 0)) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(company.getTitle());
                baseDescriptionObject.setDescription(ConvertHelper.convertDateToString(company.getFoundation(), this.getString(R.string.sys_date_format)));
                baseDescriptionObject.setCover(company.getCover());
                baseDescriptionObject.setId(company.getId());
                baseDescriptionObject.setObject(company);
                this.lvCompanies.getAdapter().add(baseDescriptionObject);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, CompanyActivity.this);
        }
    }

    protected void changeMode(boolean editMode, boolean selected) {
        this.validator.clear();
        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(!editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(!editMode && selected);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(editMode);
        Map<SwipeRefreshDeleteList, Integer> mp = new LinkedHashMap<>();
        mp.put(this.lvCompanies, 4);
        ControlsHelper.changeScreenIfEditMode(mp, this.viewPager, CompanyActivity.this, editMode);

        this.companyPagerAdapter.changeMode(editMode);
    }
}
