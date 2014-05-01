/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.tests.stress;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.osiam.resources.scim.User;
import org.osiam.resources.scim.X509Certificate;

public class RandomUser {

    private static final String EXTENSION_URN = "com.osiam.stress.test";

    public static User getNewUser() {
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
                .addAddresses(addresses)
                .setDisplayName("displayName" + getRandomNumber())
                .addEmails(emails)
                .addEntitlements(entitlements)
                .setExternalId(UUID.randomUUID().toString())
                .addIms(ims)
                .setLocale("de_DE")
                .setName(name)
                .setNickName("nickname" + getRandomNumber())
                .setPassword("password" + getRandomNumber())
                .addPhoneNumbers(phoneNumbers)
                .addPhotos(photos)
                .setPreferredLanguage("german")
                .setProfileUrl("/user/username")
                .addRoles(roles)
                .setTimezone("DE")
                .setTitle("title" + getRandomNumber())
                .addX509Certificates(x509Certificates)
                .addExtension(extension)
                .build();
    }

    private static Address getRandomAddress() {
        Address address = new Address.Builder().setCountry("USA")
                .setFormatted("formattedAddress").setLocality("Houston")
                .setPostalCode("ab57" + getRandomNumber()).setPrimary(false).setRegion("Texas")
                .setStreetAddress("Main Street. " + getRandomNumber()).setType(Address.Type.HOME)
                .build();

        return address;
    }

    private static Email getRandomEmail() {
        Email email = new Email.Builder().setPrimary(true)
                .setValue("my" + getRandomNumber() + "@mail.com").setType(Email.Type.HOME).build();
        return email;
    }

    private static Entitlement getRandomEntitilement() {
        Entitlement entitlement = new Entitlement.Builder().setPrimary(true)
                .setType(new Entitlement.Type("not irrelevant"))
                .setValue("some entitlement" + getRandomNumber()).build();
        return entitlement;
    }

    private static Im getRandomIm() {
        Im im = new Im.Builder().setPrimary(true).setType(Im.Type.GTALK)
                .setValue("gtalk" + getRandomNumber()).build();
        return im;
    }

    private static Name getRandomName() {
        Name name = new Name.Builder().setFamilyName("Simpson")
                .setFormatted("formatted" + getRandomNumber()).setGivenName("Homer")
                .setHonorificPrefix("Dr.").setHonorificSuffix("Mr.")
                .setMiddleName("J").build();
        return name;
    }

    private static PhoneNumber getRandomPhoneNumber() {
        PhoneNumber phoneNumber = new PhoneNumber.Builder().setPrimary(true)
                .setType(PhoneNumber.Type.WORK)
                .setValue("00" + getRandomNumber() + getRandomNumber() + getRandomNumber()).build();
        return phoneNumber;
    }

    private static Photo getRandomPhoto() {
        Photo photo = null;
        try {
            photo = new Photo.Builder().setPrimary(true)
                    .setType(Photo.Type.PHOTO).setValue(new URI("username" + getRandomNumber() + ".jpg")).build();
        } catch (URISyntaxException e) {
        }
        return photo;
    }

    private static Role getRandomRole() {
        Role role = new Role.Builder().setPrimary(true).setValue("user_role" + getRandomNumber())
                .build();
        return role;
    }

    private static X509Certificate getRandomX509Certificate() {
        X509Certificate x509Certificate = new X509Certificate.Builder()
                .setPrimary(true).setValue("x509Certificat" + getRandomNumber()).build();
        return x509Certificate;
    }

    private static Extension getRandomExtension() {
        Extension.Builder extensionBuilder = new Extension.Builder(EXTENSION_URN);
        
        String gender = getRandomNumber()%2 == 0 ? "male" : "female";
        extensionBuilder.setField("gender", gender);
        extensionBuilder.setField("age", new BigInteger("" + getRandomNumber()));
        return extensionBuilder.build();
    }

    private static int getRandomNumber() {
        return (int) (Math.random() * 99 + 1);
    }
}
