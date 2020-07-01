package com.application.dsi.dataClass;

import java.io.Serializable;

public class Employee implements Serializable {
    private String name;
    private String fatherName;
    private String motherName;
    private String dob;
    private String panNo;
    private String aadhaarNo;
    private String voterNo;
    private String rationNo;
    private String nationality;
    private String house_no;
    private String street_no;
    private String block;
    private String post;
    private String landmark;
    private String village;
    private String postOffice;
    private String policeStation;
    private String district;
    private String city;
    private String pinCode;
    private String gender;
    private String mobile;
    private String email;
    private String bankName;
    private String bankBranch;
    private String blockCoordinatorId;
    private String employeeId;
    private String stateCoordinatorId;
    private String districtCoordinatorId;
    private String distributorId;
    private String zoneCoordinatorId;
    private String userId;

    public Employee(String name, String fatherName, String motherName, String dob, String panNo, String aadhaarNo, String voterNo, String rationNo, String nationality, String house_no, String street_no, String block, String landmark, String village, String postOffice, String policeStation, String district, String city, String pinCode, String mobile, String email, String bankName, String bankBranch, String blockCoordinatorId, String employeeId, String stateCoordinatorId, String districtCoordinatorId, String distributorId, String zoneCoordinatorId) {
        this.name = name;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.dob = dob;
        this.panNo = panNo;
        this.aadhaarNo = aadhaarNo;
        this.voterNo = voterNo;
        this.rationNo = rationNo;
        this.nationality = nationality;
        this.house_no = house_no;
        this.street_no = street_no;
        this.block = block;
        this.landmark = landmark;
        this.village = village;
        this.postOffice = postOffice;
        this.policeStation = policeStation;
        this.district = district;
        this.city = city;
        this.pinCode = pinCode;
        this.mobile = mobile;
        this.email = email;
        this.bankName = bankName;
        this.bankBranch = bankBranch;
        this.blockCoordinatorId = blockCoordinatorId;
        this.employeeId = employeeId;
        this.stateCoordinatorId = stateCoordinatorId;
        this.districtCoordinatorId = districtCoordinatorId;
        this.distributorId = distributorId;
        this.zoneCoordinatorId = zoneCoordinatorId;
    }

    public Employee() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getBlockCoordinatorId() {
        return blockCoordinatorId;
    }

    public void setBlockCoordinatorId(String blockCoordinatorId) {
        this.blockCoordinatorId = blockCoordinatorId;
    }

    public String getStateCoordinatorId() {
        return stateCoordinatorId;
    }

    public void setStateCoordinatorId(String stateCoordinatorId) {
        this.stateCoordinatorId = stateCoordinatorId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDistrictCoordinatorId() {
        return districtCoordinatorId;
    }

    public void setDistrictCoordinatorId(String districtCoordinatorId) {
        this.districtCoordinatorId = districtCoordinatorId;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getZoneCoordinatorId() {
        return zoneCoordinatorId;
    }

    public void setZoneCoordinatorId(String zoneCoordinatorId) {
        this.zoneCoordinatorId = zoneCoordinatorId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public String getAadhaarNo() {
        return aadhaarNo;
    }

    public void setAadhaarNo(String aadhaarNo) {
        this.aadhaarNo = aadhaarNo;
    }

    public String getVoterNo() {
        return voterNo;
    }

    public void setVoterNo(String voterNo) {
        this.voterNo = voterNo;
    }

    public String getRationNo() {
        return rationNo;
    }

    public void setRationNo(String rationNo) {
        this.rationNo = rationNo;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getHouse_no() {
        return house_no;
    }

    public void setHouse_no(String house_no) {
        this.house_no = house_no;
    }

    public String getStreet_no() {
        return street_no;
    }

    public void setStreet_no(String street_no) {
        this.street_no = street_no;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getPostOffice() {
        return postOffice;
    }

    public void setPostOffice(String postOffice) {
        this.postOffice = postOffice;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }
}
