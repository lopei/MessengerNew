package com.anotap.messenger.api.interfaces;

import android.support.annotation.CheckResult;

import java.util.Collection;
import java.util.List;

import com.anotap.messenger.api.model.Items;
import com.anotap.messenger.api.model.VKApiCity;
import com.anotap.messenger.api.model.VKApiCountry;
import com.anotap.messenger.api.model.database.ChairDto;
import com.anotap.messenger.api.model.database.FacultyDto;
import com.anotap.messenger.api.model.database.SchoolClazzDto;
import com.anotap.messenger.api.model.database.SchoolDto;
import com.anotap.messenger.api.model.database.UniversityDto;
import io.reactivex.Single;

/**
 * Created by admin on 04.01.2017.
 * phoenix
 */
public interface IDatabaseApi {

    @CheckResult
    Single<List<VKApiCity>> getCitiesById(Collection<Integer> cityIds);

    @CheckResult
    Single<Items<VKApiCountry>> getCountries(Boolean needAll, String code, Integer offset, Integer count);

    @CheckResult
    Single<List<SchoolClazzDto>> getSchoolClasses(Integer countryId);

    @CheckResult
    Single<Items<ChairDto>> getChairs(int facultyId, Integer offset, Integer count);

    @CheckResult
    Single<Items<FacultyDto>> getFaculties(int universityId, Integer offset, Integer count);

    @CheckResult
    Single<Items<UniversityDto>> getUniversities(String query, Integer countryId, Integer cityId,
                                                 Integer offset, Integer count);

    @CheckResult
    Single<Items<SchoolDto>> getSchools(String query, int cityId, Integer offset, Integer count);

    @CheckResult
    Single<Items<VKApiCity>> getCities(int countryId, Integer regionId, String query, Boolean needAll, Integer offset, Integer count);
}
