package com.telemedicine.matrika.util.extensions;

import android.location.Address;
import android.location.Geocoder;
import com.telemedicine.matrika.base.AppController;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationExtensions {

    public static String getLocationData(double lat, double lng, String key){
        Geocoder geocoder = new Geocoder(AppController.getContext(),  Locale.US);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 5);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            for (Address adr : addresses) {
                switch (key){
                    case "address": if (adr.getAddressLine(adr.getMaxAddressLineIndex()) != null && adr.getAddressLine(adr.getMaxAddressLineIndex()).length() > 0) return adr.getAddressLine(adr.getMaxAddressLineIndex());
                    case "city": if (adr.getLocality() != null && adr.getLocality().length() > 0) return adr.getLocality();
                    case "state": if (adr.getAdminArea() != null && adr.getAdminArea().length() > 0) return adr.getAdminArea();
                    case "postalCode": if (adr.getPostalCode() != null && adr.getPostalCode().length() > 0) return adr.getPostalCode();
                    case "country": if (adr.getCountryName() != null && adr.getCountryName().length() > 0) return adr.getCountryName();
                }
            }
        }
        return null;
    }

    public static com.telemedicine.matrika.model.address.Address getAddress(double lat, double lng){
        try {
            Geocoder geocoder = new Geocoder(AppController.getContext(), Locale.US);
            /**
             * Here 1 represent max location result to returned,
             * by documents it recommended 1 to 5
             **/
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            com.telemedicine.matrika.model.address.Address address = new com.telemedicine.matrika.model.address.Address();
            address.setAddress(addresses.get(0).getAddressLine(addresses.get(0).getMaxAddressLineIndex()));
            address.setDistrict(addresses.get(0).getLocality());
            address.setUpazila(addresses.get(0).getAdminArea());
            address.setPostalCode(addresses.get(0).getPostalCode());
            address.setCountry(addresses.get(0).getCountryName());
            return address;
        }
        catch (IOException e) {
            e.printStackTrace();
            return new com.telemedicine.matrika.model.address.Address();
        }
    }
}
