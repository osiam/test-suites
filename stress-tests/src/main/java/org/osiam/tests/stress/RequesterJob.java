package org.osiam.tests.stress;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Entitlement;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Im;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.PhoneNumber;
import org.osiam.resources.scim.Photo;
import org.osiam.resources.scim.Role;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;
import org.osiam.resources.scim.X509Certificate;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.base.Strings;

public class RequesterJob implements Job {

    private OsiamContext osiamContext;
    private String jobName;
    private static final String EXTENSION_URN = "com.osiam.stress.test";

    public RequesterJob() {
        osiamContext = OsiamContext.getInstance();
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        jobName = context.getJobDetail().getKey().getName();
        try {
            int i = (int) (Math.random() * 30 + 1);

            if (i > 0 && i <= 10) {
                createNewUser();
            } else if (i > 10 && i <= 18) {
                searchUser();
            } else if (i > 18 && i <= 26) {
                getUser();
            } else if (i > 26 && i <= 28) {
                updateUser();
            } else if (i > 28 && i <= 29) {
                replaceUser();
            } else if(i == 30){
                deleteUser();
            }

        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("Error at Job " + jobName + ": " + e.getMessage());
        }
    }

    private void createNewUser() {
        System.out.println(jobName + ": Creating a new User");

        User user = getRandomUser();
        osiamContext.getConnector().createUser(user, osiamContext.getValidAccessToken());
    }

    private User getRandomUser() {
        List<Address> addresses = new ArrayList<Address>();
        addresses.add(getRandomAddress());

        List<Email> emails = new ArrayList<Email>();
        emails.add(getRandomEmail());

        List<Entitlement> entitlements = new ArrayList<Entitlement>();
        entitlements.add(getRandomEntitilement());

        List<Im> ims = new ArrayList<Im>();
        ims.add(getRandomIm());

        Name name = getRandomName();

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
        phoneNumbers.add(getRandomPhoneNumber());

        List<Photo> photos = new ArrayList<Photo>();
        photos.add(getRandomPhoto());

        List<Role> roles = new ArrayList<Role>();
        roles.add(getRandomRole());

        List<X509Certificate> x509Certificates = new ArrayList<X509Certificate>();
        x509Certificates.add(getRandomX509Certificate());

        Extension extension = getRandomExtension();

        return new User.Builder(UUID.randomUUID().toString())
                .setActive(true)
                .setAddresses(addresses)
                .setDisplayName("displayName" + getRandomNumber())
                .setEmails(emails)
                .setEntitlements(entitlements)
                .setExternalId(UUID.randomUUID().toString())
                .setIms(ims)
                .setLocale("de_DE")
                .setName(name)
                .setNickName("nickname" + getRandomNumber())
                .setPassword("password" + getRandomNumber())
                .setPhoneNumbers(phoneNumbers)
                .setPhotos(photos)
                .setPreferredLanguage("german")
                .setProfileUrl("/user/username")
                .setRoles(roles)
                .setTimezone("DE")
                .setTitle("title" + getRandomNumber())
                .setX509Certificates(x509Certificates)
                // .addExtension(extension) TODO extension doesn't exist at the moment
                .build();
    }

    private Address getRandomAddress() {
        Address address = new Address.Builder().setCountry("USA")
                .setFormatted("formattedAddress").setLocality("Houston")
                .setPostalCode("ab57" + getRandomNumber()).setPrimary(false).setRegion("Texas")
                .setStreetAddress("Main Street. " + getRandomNumber()).setType(Address.Type.HOME)
                .build();

        return address;
    }

    private Email getRandomEmail() {
        Email email = new Email.Builder().setPrimary(true)
                .setValue("my" + getRandomNumber() + "@mail.com").setType(Email.Type.HOME).build();
        return email;
    }

    private Entitlement getRandomEntitilement() {
        Entitlement entitlement = new Entitlement.Builder().setPrimary(true)
                .setType(new Entitlement.Type("not irrelevant"))
                .setValue("some entitlement" + getRandomNumber()).build();
        return entitlement;
    }

    private Im getRandomIm() {
        Im im = new Im.Builder().setPrimary(true).setType(Im.Type.GTALK)
                .setValue("gtalk" + getRandomNumber()).build();
        return im;
    }

    private Name getRandomName() {
        Name name = new Name.Builder().setFamilyName("Simpson")
                .setFormatted("formatted" + getRandomNumber()).setGivenName("Homer")
                .setHonorificPrefix("Dr.").setHonorificSuffix("Mr.")
                .setMiddleName("J").build();
        return name;
    }

    private PhoneNumber getRandomPhoneNumber() {
        PhoneNumber phoneNumber = new PhoneNumber.Builder().setPrimary(true)
                .setType(PhoneNumber.Type.WORK)
                .setValue("00" + getRandomNumber() + getRandomNumber() + getRandomNumber()).build();
        return phoneNumber;
    }

    private Photo getRandomPhoto() {
        Photo photo = new Photo.Builder().setPrimary(true)
                .setType(Photo.Type.PHOTO).setValue("username" + getRandomNumber() + ".jpg").build();
        return photo;
    }

    private Role getRandomRole() {
        Role role = new Role.Builder().setPrimary(true).setValue("user_role" + getRandomNumber())
                .build();
        return role;
    }

    private X509Certificate getRandomX509Certificate() {
        X509Certificate x509Certificate = new X509Certificate.Builder()
                .setPrimary(true).setValue("x509Certificat" + getRandomNumber()).build();
        return x509Certificate;
    }

    private Extension getRandomExtension() {
        Extension extension = new Extension(EXTENSION_URN);
        extension.addOrUpdateField("gender", "female");
        extension.addOrUpdateField("age", new BigInteger("" + getRandomNumber()));
        return extension;
    }

    private int getRandomNumber() {
        return (int) (Math.random() * 99 + 1);
    }

    private void updateUser() {
        System.out.println(jobName + ": Updating a User");
        String userId = osiamContext.retrieveSingleUserId();
        if (!Strings.isNullOrEmpty(userId)) {
            UpdateUser updateUser = new UpdateUser.Builder().updateExternalId(UUID.randomUUID().toString()).build();
            osiamContext.getConnector().updateUser(userId, updateUser, osiamContext.getValidAccessToken());
        }
    }

    private void replaceUser() {
        System.out.println(jobName + ": Replace a User");
        String userId = osiamContext.retrieveSingleUserId();
        if (!Strings.isNullOrEmpty(userId)) {
            User replaceUser = getRandomUser();
            osiamContext.getConnector().replaceUser(userId, replaceUser, osiamContext.getValidAccessToken());
        }
    }

    private void deleteUser() {
        System.out.println(jobName + ": deleting a User");
        System.out.println(jobName + ": get a Users");
        String userId = osiamContext.retrieveSingleUserId();
        if (!Strings.isNullOrEmpty(userId)) {
            osiamContext.getConnector().deleteUser(userId, osiamContext.getValidAccessToken());
        }
    }

    private void searchUser() throws UnsupportedEncodingException {
        System.out.println(jobName + ": searching for Users");
        String query = getCompletUserQueryString();
        SCIMSearchResult<User> queryResult = osiamContext.getConnector().searchUsers("filter=" + query,
                osiamContext.getValidAccessToken());
        if (queryResult.getTotalResults() == 0) {
            queryResult = osiamContext.getConnector().searchUsers("filter=",
                    osiamContext.getValidAccessToken());
        }
        osiamContext.setListOfUsers(queryResult.getResources());
    }

    private String getCompletUserQueryString() throws UnsupportedEncodingException {
        String encoded = null;
        encoded = URLEncoder.encode("active eq \"true\""
                + " and addresses.postalCode co \"" + getRandomNumber() + "\""
                + " and addresses.primary eq \"true\"", "UTF-8");
        return encoded;
    }

    private void getUser() {
        System.out.println(jobName + ": get a Users");
        String userId = osiamContext.retrieveSingleUserId();
        if (!Strings.isNullOrEmpty(userId)) {
            osiamContext.getConnector().getUser(userId, osiamContext.getValidAccessToken());
        }
    }

}
