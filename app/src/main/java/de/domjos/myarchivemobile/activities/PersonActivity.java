package de.domjos.myarchivemobile.activities;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.PersonPagerAdapter;

public final class PersonActivity extends AbstractActivity {
    private SwipeRefreshDeleteList lvPersons;
    private PersonPagerAdapter personPagerAdapter;
    private BottomNavigationView bottomNavigationView;

    private Person person = null;

    public PersonActivity() {
        super(R.layout.person_activity);
    }


    @Override
    protected void initActions() {
        this.lvPersons.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                PersonActivity.this.reload();
            }
        });
        this.lvPersons.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                Person person = (Person) listObject.getObject();
                MainActivity.GLOBALS.getDatabase().deleteItem(person);
                changeMode(false, false);
                personPagerAdapter.setMediaObject(new Person());
            }
        });
        this.lvPersons.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                person = (Person) listObject.getObject();
                personPagerAdapter.setMediaObject(person);
                changeMode(false, true);
            }
        });


        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    this.changeMode(true, false);
                    this.personPagerAdapter.setMediaObject(new Person());
                    this.person = null;
                    break;
                case R.id.cmdEdit:
                    if(this.person != null) {
                        this.changeMode(true, true);
                        this.personPagerAdapter.setMediaObject(this.person);
                    }
                    break;
                case R.id.cmdCancel:
                    this.changeMode(false, false);
                    this.person = null;
                    this.reload();
                    break;
                case R.id.cmdSave:
                    Person person = this.personPagerAdapter.getMediaObject();
                    if(this.person!=null) {
                        person.setId(this.person.getId());
                    }
                    MainActivity.GLOBALS.getDatabase().insertOrUpdatePerson(person, "", 0);
                    this.changeMode(false, false);
                    this.person = null;
                    this.reload();
                    break;
            }
            return true;
        });
    }

    @Override
    protected void initControls() {
        this.lvPersons = this.findViewById(R.id.lvPersons);
        this.bottomNavigationView = this.findViewById(R.id.navigationView);

        TabLayout tabLayout = this.findViewById(R.id.tabLayout);
        ViewPager viewPager = this.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);

        this.personPagerAdapter = new PersonPagerAdapter(this.getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(this.personPagerAdapter);

        for(int i = 0; i<=tabLayout.getTabCount()-1; i++) {
            tabLayout.setScrollPosition(i, 0f, true);
            viewPager.setCurrentItem(i);
        }
        tabLayout.setScrollPosition(0, 0f, true);
        viewPager.setCurrentItem(0);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_person_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_image_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_format_list_bulleted_black_24dp);

        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(false);
    }

    @Override
    protected void reload() {
        try {
            this.lvPersons.getAdapter().clear();
            for(Person person : MainActivity.GLOBALS.getDatabase().getPersons("", 0)) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(String.format("%s %s", person.getFirstName(), person.getLastName()).trim());
                baseDescriptionObject.setDescription(Converter.convertDateToString(person.getBirthDate(), this.getString(R.string.sys_date_format)));
                baseDescriptionObject.setCover(person.getImage());
                baseDescriptionObject.setObject(person);
                this.lvPersons.getAdapter().add(baseDescriptionObject);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, PersonActivity.this);
        }
    }

    protected void changeMode(boolean editMode, boolean selected) {
        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(!editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(!editMode && selected);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(editMode);

        this.personPagerAdapter.changeMode(editMode);
    }
}
