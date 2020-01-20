package de.domjos.myarchivemobile.activities;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.CompanyPagerAdapter;
import de.domjos.myarchivemobile.adapter.PersonPagerAdapter;

public final class CompanyActivity extends AbstractActivity {
    private SwipeRefreshDeleteList lvCompanies;
    private CompanyPagerAdapter companyPagerAdapter;
    private BottomNavigationView bottomNavigationView;

    private Company company = null;
    private Validator validator;

    public CompanyActivity() {
        super(R.layout.company_activity);
    }


    @Override
    protected void initActions() {
        this.lvCompanies.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                CompanyActivity.this.reload();
            }
        });
        this.lvCompanies.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                Company company = (Company) listObject.getObject();
                MainActivity.GLOBALS.getDatabase().deleteItem(company);
                changeMode(false, false);
                companyPagerAdapter.setMediaObject(new Company());
            }
        });
        this.lvCompanies.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                company = (Company) listObject.getObject();
                companyPagerAdapter.setMediaObject(company);
                changeMode(false, true);
            }
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
                        MainActivity.GLOBALS.getDatabase().insertOrUpdateCompany(company, "", 0);
                        this.changeMode(false, false);
                        this.company = null;
                        this.reload();
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
        ViewPager viewPager = this.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);

        this.companyPagerAdapter = new CompanyPagerAdapter(this.getSupportFragmentManager(), getApplicationContext());
        this.validator = this.companyPagerAdapter.initValidator();
        viewPager.setAdapter(this.companyPagerAdapter);

        for(int i = 0; i<=tabLayout.getTabCount()-1; i++) {
            tabLayout.setScrollPosition(i, 0f, true);
            viewPager.setCurrentItem(i);
        }
        tabLayout.setScrollPosition(0, 0f, true);
        viewPager.setCurrentItem(0);

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
                baseDescriptionObject.setDescription(Converter.convertDateToString(company.getFoundation(), this.getString(R.string.sys_date_format)));
                baseDescriptionObject.setCover(company.getCover());
                baseDescriptionObject.setObject(company);
                this.lvCompanies.getAdapter().add(baseDescriptionObject);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, CompanyActivity.this);
        }
    }

    protected void changeMode(boolean editMode, boolean selected) {
        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(!editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(!editMode && selected);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(editMode);

        this.companyPagerAdapter.changeMode(editMode);
    }
}
