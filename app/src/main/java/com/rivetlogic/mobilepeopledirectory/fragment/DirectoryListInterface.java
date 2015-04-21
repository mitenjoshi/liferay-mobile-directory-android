package com.rivetlogic.mobilepeopledirectory.fragment;

import com.rivetlogic.mobilepeopledirectory.model.User;

/**
 * Created by lorenz on 4/17/15.
 */
public interface DirectoryListInterface {
    void onItemClicked(User user);

    void onFavoritesClicked();

    void logout();

    void about();

}
