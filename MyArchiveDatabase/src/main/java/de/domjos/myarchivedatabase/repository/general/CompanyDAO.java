package de.domjos.myarchivedatabase.repository.general;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.company.CompanyWithAlbums;
import de.domjos.myarchivedatabase.model.general.company.CompanyWithBooks;
import de.domjos.myarchivedatabase.model.general.company.CompanyWithGames;
import de.domjos.myarchivedatabase.model.general.company.CompanyWithMovies;
import de.domjos.myarchivedatabase.model.general.company.CompanyWithSongs;

@Dao
public interface CompanyDAO {
    @Query("SELECT * FROM companies")
    List<Company> getAllCompanies();

    @Query("SELECT * FROM companies WHERE id=:id")
    Company getCompany(long id);

    @Query("SELECT * FROM companies WHERE title=:title")
    Company getCompany(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertCompanies(Company... companies);

    @Update
    void updateCompanies(Company... companies);

    @Delete
    void deleteCompanies(Company... companies);

    @Transaction
    @Query("SELECT * FROM companies")
    List<CompanyWithAlbums> getAllCompaniesWithAlbums();

    @Transaction
    @Query("SELECT * FROM companies WHERE id=:id")
    CompanyWithAlbums getCompanyWithAlbums(long id);

    @Transaction
    @Query("SELECT * FROM companies")
    List<CompanyWithSongs> getAllCompaniesWithSongs();

    @Transaction
    @Query("SELECT * FROM companies WHERE id=:id")
    CompanyWithSongs getCompanyWithSongs(long id);

    @Transaction
    @Query("SELECT * FROM companies")
    List<CompanyWithMovies> getAllCompaniesWithMovies();

    @Transaction
    @Query("SELECT * FROM companies WHERE id=:id")
    CompanyWithMovies getCompanyWithMovies(long id);

    @Transaction
    @Query("SELECT * FROM companies")
    List<CompanyWithBooks> getAllCompaniesWithBooks();

    @Transaction
    @Query("SELECT * FROM companies WHERE id=:id")
    CompanyWithBooks getCompanyWithBooks(long id);

    @Transaction
    @Query("SELECT * FROM companies")
    List<CompanyWithGames> getAllCompaniesWithGames();

    @Transaction
    @Query("SELECT * FROM companies WHERE id=:id")
    CompanyWithGames getCompanyWithGames(long id);
}
