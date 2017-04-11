package com.policestrategies.calm_stop.officer.profile;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

/**
 * Uploads modified information to Firebase.
 * @author Talal Abou Haiba
 */
class ProfileUpdater {

    private String mRemoteEmail;
    private String mRemoteFirstName;
    private String mRemoteLastName;
    private String mRemoteBadgeNumber;
    private String mRemotePhotoPath;
    private int mRemoteGender;

    private DatabaseReference mProfileReference;
    private StorageReference mStorageReference;
    private String mOfficerUid;

    ProfileUpdater(String email, String firstName, String lastName, String badge, String photo,
                   int gender, DatabaseReference database, StorageReference storage, String uid) {
        mRemoteEmail = email;
        mRemoteFirstName = firstName;
        mRemoteLastName = lastName;
        mRemoteBadgeNumber = badge;
        mRemotePhotoPath = photo;
        mRemoteGender = gender;
        mProfileReference = database;
        mStorageReference = storage;
        mOfficerUid = uid;
    }

    void updateProfile(String email, String firstName, String lastName, String badge, Uri photo,
                       int gender) {
        updateEmail(email);
        updateFirstName(firstName);
        updateLastName(lastName);
        updateBadgeNumber(badge);
        updatePhotoFile(photo);
        updateGender(gender);
    }

    private void updateEmail(String newEmail) {
        if (!mRemoteEmail.equals(newEmail)) {

        }
    }

    private void updateFirstName(String newFirstName) {
        if (!mRemoteFirstName.equals(newFirstName)) {
            mProfileReference.child("first_name").setValue(newFirstName);
        }
    }

    private void updateLastName(String newLastName) {
        if (!mRemoteLastName.equals(newLastName)) {
            mProfileReference.child("last_name").setValue(newLastName);
        }
    }

    private void updateBadgeNumber(String newBadgeNumber) {
        if (!mRemoteBadgeNumber.equals(newBadgeNumber)) {
            mProfileReference.child("badge").setValue(newBadgeNumber);
        }
    }

    private void updatePhotoPath(String newPhotoPath) {
        if (!mRemotePhotoPath.equals(newPhotoPath)) {
            mProfileReference.child("photo").setValue(newPhotoPath);
        }
    }

    private void updatePhotoFile(Uri newPhoto) {
        if (newPhoto == null) {
            return;
        }
        String photoPath = "images/profile/" + mOfficerUid;
        updatePhotoPath(photoPath);

        StorageReference profilePictureRef = mStorageReference.child(photoPath);
        profilePictureRef.putFile(newPhoto);
    }

    private void updateGender(int newGender) {
        if (mRemoteGender != newGender) {
            mProfileReference.child("gender").setValue(newGender);
        }
    }

} // end class ProfileUpdater
