package de.domjos.myarchivedatabase.repository.general;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import de.domjos.myarchivedatabase.AppDatabase;
import de.domjos.myarchivedatabase.converter.DrawableConverter;
import de.domjos.myarchivedatabase.model.general.company.Company;

@RunWith(AndroidJUnit4.class)

public class CompanyDAOTest {
    private CompanyDAO companyDAO;
    private AppDatabase appDatabase;
    private final String title = "Test AG";

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();
        this.companyDAO = this.appDatabase.companyDAO();
    }

    @After
    public void closeDb() {
        this.appDatabase.close();
    }

    @Test
    public void testInsertAndDelete() {

        // insert company
        Company company = new Company();
        company.setTitle(this.title);
        long id = this.companyDAO.insertCompanies(company)[0];

        // get company
        List<Company> companies = this.companyDAO.getAllCompanies();
        Assert.assertEquals(companies.size(), 1);
        company = this.companyDAO.getCompany(this.title);
        Assert.assertEquals(company.getTitle(), this.title);
        Assert.assertEquals(id, company.getId());

        // delete company
        this.companyDAO.deleteCompanies(company);
        companies = this.companyDAO.getAllCompanies();
        Assert.assertEquals(companies.size(), 0);
    }

    @Test
    public void testInsertUpdateAndDelete() {

        // insert company
        Company company = new Company();
        company.setTitle(this.title);
        long id = this.companyDAO.insertCompanies(company)[0];

        // get company
        List<Company> companies = this.companyDAO.getAllCompanies();
        Assert.assertEquals(companies.size(), 1);
        company = this.companyDAO.getCompany(this.title);
        Assert.assertEquals(company.getTitle(), this.title);
        Assert.assertEquals(id, company.getId());

        // update company
        String newTest = "Test2";
        company.setTitle(newTest);
        this.companyDAO.updateCompanies(company);
        company = this.companyDAO.getCompany(company.getId());
        Assert.assertEquals(company.getTitle(), newTest);

        // delete company
        this.companyDAO.deleteCompanies(company);
        companies = this.companyDAO.getAllCompanies();
        Assert.assertEquals(companies.size(), 0);
    }

    @Test
    public void testDateConverter() {
        Date date = new Date();
        Company company = new Company();
        company.setTitle(this.title);
        company.setFoundation(date);
        this.companyDAO.insertCompanies(company);

        Company tmp = this.companyDAO.getCompany(this.title);
        Assert.assertEquals(tmp.getFoundation(), date);
    }
}
