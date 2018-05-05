package com.anotap.messenger.domain.impl;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.api.model.VKApiCity;
import com.anotap.messenger.api.model.VKApiCountry;
import com.anotap.messenger.api.model.database.ChairDto;
import com.anotap.messenger.api.model.database.FacultyDto;
import com.anotap.messenger.api.model.database.SchoolClazzDto;
import com.anotap.messenger.api.model.database.SchoolDto;
import com.anotap.messenger.api.model.database.UniversityDto;
import com.anotap.messenger.db.interfaces.IDatabaseStore;
import com.anotap.messenger.db.model.entity.CountryEntity;
import com.anotap.messenger.domain.IDatabaseInteractor;
import com.anotap.messenger.model.City;
import com.anotap.messenger.model.database.Chair;
import com.anotap.messenger.model.database.Country;
import com.anotap.messenger.model.database.Faculty;
import com.anotap.messenger.model.database.School;
import com.anotap.messenger.model.database.SchoolClazz;
import com.anotap.messenger.model.database.University;
import com.anotap.messenger.util.Utils;
import io.reactivex.Single;

/**
 * Created by Ruslan Kolbasa on 20.09.2017.
 * phoenix
 */
public class DatabaseInteractor implements IDatabaseInteractor {

    private final IDatabaseStore cache;
    private final INetworker networker;

    public DatabaseInteractor(IDatabaseStore cache, INetworker networker) {
        this.cache = cache;
        this.networker = networker;
    }

    @Override
    public Single<List<Chair>> getChairs(int accoutnId, int facultyId, int count, int offset) {
        return networker.vkDefault(accoutnId)
                .database()
                .getChairs(facultyId, offset, count)
                .map(items -> {
                    List<ChairDto> dtos = Utils.listEmptyIfNull(items.getItems());
                    List<Chair> chairs = new ArrayList<>(dtos.size());

                    for(ChairDto dto : dtos){
                        chairs.add(new Chair(dto.id, dto.title));
                    }

                    return chairs;
                });
    }

    @Override
    public Single<List<Country>> getCountries(int accountId, boolean ignoreCache) {
        if(ignoreCache){
            return networker.vkDefault(accountId)
                    .database()
                    .getCountries(true, null, null, 1000)
                    .flatMap(items -> {
                        List<VKApiCountry> dtos = Utils.listEmptyIfNull(items.getItems());
                        List<CountryEntity> dbos = new ArrayList<>(dtos.size());
                        List<Country> countries = new ArrayList<>(dbos.size());

                        for(VKApiCountry dto : dtos){
                            dbos.add(new CountryEntity(dto.id, dto.title));
                            countries.add(new Country(dto.id, dto.title));
                        }

                        return cache.storeCountries(accountId, dbos)
                                .andThen(Single.just(countries));
                    });
        }

        return cache.getCountries(accountId)
                .flatMap(dbos -> {
                    if(dbos.size() > 0){
                        List<Country> countries = new ArrayList<>(dbos.size());
                        for(CountryEntity dbo : dbos){
                            countries.add(new Country(dbo.getId(), dbo.getTitle()));
                        }

                        return Single.just(countries);
                    }

                    return getCountries(accountId, true);
                });
    }

    @Override
    public Single<List<City>> getCities(int accountId, int countryId, String q, boolean needAll, int count, int offset) {
        return networker.vkDefault(accountId)
                .database()
                .getCities(countryId, null, q, needAll, offset, count)
                .map(items -> {
                    List<VKApiCity> dtos = Utils.listEmptyIfNull(items.getItems());
                    List<City> cities = new ArrayList<>(dtos.size());

                    for(VKApiCity dto : dtos){
                        cities.add(new City(dto.id, dto.title)
                        .setArea(dto.area)
                        .setImportant(dto.important)
                        .setRegion(dto.region));
                    }

                    return cities;
                });
    }

    @Override
    public Single<List<Faculty>> getFaculties(int accountId, int universityId, int count, int offset) {
        return networker.vkDefault(accountId)
                .database()
                .getFaculties(universityId, offset, count)
                .map(items -> {
                    List<FacultyDto> dtos = Utils.listEmptyIfNull(items.getItems());
                    List<Faculty> faculties = new ArrayList<>(dtos.size());

                    for(FacultyDto dto : dtos){
                        faculties.add(new Faculty(dto.id, dto.title));
                    }

                    return faculties;
                });
    }

    @Override
    public Single<List<SchoolClazz>> getSchoolClasses(int accountId, int countryId) {
        return networker.vkDefault(accountId)
                .database()
                .getSchoolClasses(countryId)
                .map(dtos -> {
                    List<SchoolClazz> clazzes = new ArrayList<>(dtos.size());

                    for(SchoolClazzDto dto : dtos){
                        clazzes.add(new SchoolClazz(dto.id, dto.title));
                    }

                    return clazzes;
                });
    }

    @Override
    public Single<List<School>> getSchools(int accountId, int cityId, String q, int count, int offset) {
        return networker.vkDefault(accountId)
                .database()
                .getSchools(q, cityId, offset, count)
                .map(items -> {
                    List<SchoolDto> dtos = Utils.listEmptyIfNull(items.getItems());
                    List<School> schools = new ArrayList<>(dtos.size());

                    for(SchoolDto dto : dtos){
                        schools.add(new School(dto.id, dto.title));
                    }

                    return schools;
                });
    }

    @Override
    public Single<List<University>> getUniversities(int accoutnId, String filter, Integer cityId, Integer countyId, int count, int offset) {
        return networker.vkDefault(accoutnId)
                .database()
                .getUniversities(filter, countyId, cityId, offset, count)
                .map(items -> {
                    List<UniversityDto> dtos = Utils.listEmptyIfNull(items.getItems());
                    List<University> universities = new ArrayList<>(dtos.size());

                    for(UniversityDto dto : dtos){
                        universities.add(new University(dto.id, dto.title));
                    }

                    return universities;
                });
    }
}