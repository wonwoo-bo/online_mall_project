package com.mall.module.user.service;

import com.mall.module.user.dto.AddressDTO;
import com.mall.module.user.dto.RegisterDTO;
import com.mall.module.user.dto.UpdateUserDTO;
import com.mall.module.user.entity.MemberInfo;
import com.mall.module.user.entity.MemberType;
import com.mall.module.user.entity.PointsRecord;
import com.mall.module.user.entity.User;
import com.mall.module.user.entity.UserAddress;
import com.mall.module.user.entity.UserRole;

import java.util.List;

public interface UserService {
    void register(RegisterDTO registerDTO);
    User login(String username, String password);
    User getUserInfo(Integer id);
    void updateUserInfo(Integer id, UpdateUserDTO updateUserDTO);
    void updatePassword(Integer id, String oldPassword, String newPassword);
    void addAddress(Integer userId, AddressDTO addressDTO);
    void updateAddress(Integer userId, Integer addressId, AddressDTO addressDTO);
    void deleteAddress(Integer userId, Integer addressId);
    List<UserAddress> getAddresses(Integer userId);
    UserAddress getAddressById(Integer userId, Integer addressId);
    void setDefaultAddress(Integer userId, Integer addressId);
    
    MemberInfo getMemberInfo(Integer userId);
    void updateMemberInfo(Integer userId, MemberInfo memberInfo);
    void addPoints(Integer userId, Integer points);
    void deductPoints(Integer userId, Integer points, String description);
    List<PointsRecord> getPointsHistory(Integer userId);
    
    UserRole getUserRole(Integer userId);
    void updateUserRole(Integer userId, UserRole userRole);
    
    List<MemberType> getAllMemberTypes();
    MemberType getMemberTypeByCode(String levelCode);
    void purchaseMember(Integer userId, String levelCode);
}
