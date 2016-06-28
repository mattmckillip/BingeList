package com.example.matt.bingeList.uitls.API;

import com.example.matt.bingeList.models.Credits;
import com.example.matt.bingeList.models.Person;
import com.example.matt.bingeList.models.PersonCredits;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface PersonAPI {
    @GET("{id}?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<Person> getPersonProfile(@Path("id") String id);

    @GET("{id}/combined_credits?api_key=788bf2d4d9f5db03979efed58cbf6713")
    Call<PersonCredits> getPersonCombinedCredits(@Path("id") String id);
}
