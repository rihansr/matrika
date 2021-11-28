package com.telemedicine.matrika.model.user;

import com.google.firebase.firestore.ServerTimestamp;
import com.telemedicine.matrika.model.other.Status;
import com.telemedicine.matrika.model.address.Address;
import com.telemedicine.matrika.model.specialty.Specialty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {

    /**
     * Static Fields
     **/
    public static String ID = "id";
    public static String VERIFIED = "profileVerified";
    public static String ACTIVE = "active";
    public static String TOKEN = "token";
    public static String ROLE = "role";
    public static String RATING = "rating";
    public static String FOLLOWER = "followers";
    public static String SPECIALTY = "specialty";
    public static String PHOTO = "photo";
    public static String CHANNEL_ID = "channelId";
    public static String DEVICE_KEY = "deviceKey";

    /** Additional Info **/
    private String          id;
    private String          role;
    private String          token;
    private Status          active;
    private Boolean         profileCompleted;
    private Boolean         profileReviewed;
    private Boolean         profileVerified;
    private Boolean         phoneVerified;
    private Boolean         emailVerified;
    private Double          rating;
    private Integer         followers;
    @ServerTimestamp
    private Date            registeredAt;

    /** Personal Info **/
    private String          photo;
    private String          name;
    private String          email;
    private String          phone;
    private String          gender;
    @ServerTimestamp
    private Date            dateOfBirth;
    private Integer         age;
    private Double          height;
    private Double          weight;
    private String          maritalStatus;
    private String          nid;
    private String          nidPhoto;
    private Address         address;

    /** Academic Info **/
    private List<String>    languages;
    private List<String>    qualifications;
    private Specialty       specialty;
    private String          bmDcNo;

    /** Professional Info **/
    private String          Bio;
    private Integer         experienceYears;
    private Integer         consultPatientsNo;
    private Integer         consultationTime;
    private Integer         fees;
    private String          awards;

    /** Device **/
    private String          channelId;
    private String          deviceKey;

    /** User Request **/
    private String          requestId;

    public User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Status getActive() {
        return active;
    }

    public void setActive(Status active) {
        this.active = active;
    }

    public Boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(Boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public Boolean isProfileReviewed() {
        return profileReviewed;
    }

    public void setProfileReviewed(Boolean profileReviewed) {
        this.profileReviewed = profileReviewed;
    }

    public Boolean isProfileVerified() {
        return profileVerified;
    }

    public void setProfileVerified(Boolean profileVerified) {
        this.profileVerified = profileVerified;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getNidPhoto() {
        return nidPhoto;
    }

    public void setNidPhoto(String nidPhoto) {
        this.nidPhoto = nidPhoto;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<String> qualifications) {
        this.qualifications = qualifications;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public String getBmDcNo() {
        return bmDcNo;
    }

    public void setBmDcNo(String bmDcNo) {
        this.bmDcNo = bmDcNo;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Integer getConsultPatientsNo() {
        return consultPatientsNo;
    }

    public void setConsultPatientsNo(Integer consultPatientsNo) {
        this.consultPatientsNo = consultPatientsNo;
    }

    public Integer getConsultationTime() {
        return consultationTime;
    }

    public void setConsultationTime(Integer consultationTime) {
        this.consultationTime = consultationTime;
    }

    public Integer getFees() {
        return fees;
    }

    public void setFees(Integer fees) {
        this.fees = fees;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
