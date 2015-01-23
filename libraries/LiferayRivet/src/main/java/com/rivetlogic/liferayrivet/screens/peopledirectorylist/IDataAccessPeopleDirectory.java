package com.rivetlogic.liferayrivet.screens.peopledirectorylist;

import java.util.ArrayList;

/**
 * Created by lorenz on 1/21/15.
 */
public interface IDataAccessPeopleDirectory {

    public void updateUsers(ArrayList<User> users);

    public void updateUser(User user);

    public ArrayList<User> getUsers();

    long getModifiedDate();
}