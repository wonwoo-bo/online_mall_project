package com.mall.module.user.service.impl;

import com.mall.common.PasswordUtil;
import com.mall.module.user.dto.AddressDTO;
import com.mall.module.user.dto.RegisterDTO;
import com.mall.module.user.dto.UpdateUserDTO;
import com.mall.module.user.entity.MemberInfo;
import com.mall.module.user.entity.MemberType;
import com.mall.module.user.entity.PointsRecord;
import com.mall.module.user.entity.User;
import com.mall.module.user.entity.UserAddress;
import com.mall.module.user.entity.UserRole;
import com.mall.module.user.mapper.MemberInfoMapper;
import com.mall.module.user.mapper.MemberTypeMapper;
import com.mall.module.user.mapper.PointsRecordMapper;
import com.mall.module.user.mapper.UserAddressMapper;
import com.mall.module.user.mapper.UserMapper;
import com.mall.module.user.mapper.UserRoleMapper;
import com.mall.module.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private MemberInfoMapper memberInfoMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private MemberTypeMapper memberTypeMapper;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        User existingUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        String salt = PasswordUtil.generateSalt();
        user.setSalt(salt);
        user.setPassword(PasswordUtil.encryptPassword(registerDTO.getPassword(), salt));
        user.setPhone(registerDTO.getPhone());
        user.setNickname(registerDTO.getUsername());

        userMapper.insert(user);

        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setUserId(user.getId());
        memberInfo.setMemberLevel("非会员");
        memberInfo.setPoints(0);
        memberInfo.setExpireTime(null);
        memberInfoMapper.insert(memberInfo);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleCode("USER");
        userRole.setRoleName("普通用户");
        userRole.setPermissions("user:read,user:update,address:crud");
        userRoleMapper.insert(userRole);
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 先尝试直接比较密码（支持明文密码）
        if (password.equals(user.getPassword())) {
            return user;
        }
        
        // 如果直接比较失败，再尝试加密验证
        if (!PasswordUtil.verifyPassword(password, user.getPassword(), user.getSalt())) {
            throw new RuntimeException("用户名或密码错误");
        }

        return user;
    }

    @Override
    public User getUserInfo(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(null);
        user.setSalt(null);
        return user;
    }

    @Override
    @Transactional
    public void updateUserInfo(Integer id, UpdateUserDTO updateUserDTO) {
        User user = new User();
        user.setId(id);
        user.setNickname(updateUserDTO.getNickname());
        user.setPhone(updateUserDTO.getPhone());
        user.setAddress(updateUserDTO.getAddress());

        userMapper.update(user);
    }

    @Override
    @Transactional
    public void updatePassword(Integer id, String oldPassword, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword(), user.getSalt())) {
            throw new RuntimeException("原密码错误");
        }

        String newSalt = PasswordUtil.generateSalt();
        String newEncryptedPassword = PasswordUtil.encryptPassword(newPassword, newSalt);

        userMapper.updatePassword(id, newEncryptedPassword, newSalt);
    }

    @Override
    @Transactional
    public void addAddress(Integer userId, AddressDTO addressDTO) {
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setReceiverName(addressDTO.getReceiverName());
        address.setReceiverPhone(addressDTO.getReceiverPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetailAddress(addressDTO.getDetailAddress());
        address.setIsDefault(addressDTO.getIsDefault());

        if (address.getIsDefault() == 1) {
            userAddressMapper.clearDefault(userId);
        }

        userAddressMapper.insert(address);
    }

    @Override
    @Transactional
    public void updateAddress(Integer userId, Integer addressId, AddressDTO addressDTO) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }

        address.setReceiverName(addressDTO.getReceiverName());
        address.setReceiverPhone(addressDTO.getReceiverPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetailAddress(addressDTO.getDetailAddress());
        address.setIsDefault(addressDTO.getIsDefault());

        if (address.getIsDefault() == 1) {
            userAddressMapper.clearDefault(userId);
        }

        userAddressMapper.update(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Integer userId, Integer addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }

        userAddressMapper.delete(addressId);
    }

    @Override
    public List<UserAddress> getAddresses(Integer userId) {
        return userAddressMapper.selectByUserId(userId);
    }

    @Override
    public UserAddress getAddressById(Integer userId, Integer addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }
        return address;
    }

    @Override
    @Transactional
    public void setDefaultAddress(Integer userId, Integer addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }
        // 清除该用户的所有默认地址
        userAddressMapper.clearDefault(userId);
        // 设置当前地址为默认
        userAddressMapper.setDefault(addressId, 1);
    }

    @Override
    public MemberInfo getMemberInfo(Integer userId) {
        MemberInfo memberInfo = memberInfoMapper.selectByUserId(userId);
        if (memberInfo == null) {
            throw new RuntimeException("会员信息不存在");
        }
        return memberInfo;
    }

    @Override
    @Transactional
    public void updateMemberInfo(Integer userId, MemberInfo memberInfo) {
        MemberInfo existing = memberInfoMapper.selectByUserId(userId);
        if (existing == null) {
            throw new RuntimeException("会员信息不存在");
        }
        memberInfo.setUserId(userId);
        memberInfoMapper.updateByUserId(memberInfo);
    }

    @Override
    @Transactional
    public void addPoints(Integer userId, Integer points) {
        MemberInfo memberInfo = memberInfoMapper.selectByUserId(userId);
        if (memberInfo == null) {
            throw new RuntimeException("会员信息不存在");
        }
        memberInfo.setPoints(memberInfo.getPoints() + points);
        memberInfoMapper.updateByUserId(memberInfo);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType("ADD");
        record.setDescription("积分增加");
        record.setCreateTime(new java.util.Date());
        pointsRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void deductPoints(Integer userId, Integer points, String description) {
        MemberInfo memberInfo = memberInfoMapper.selectByUserId(userId);
        if (memberInfo == null) {
            throw new RuntimeException("会员信息不存在");
        }
        int currentPoints = memberInfo.getPoints() != null ? memberInfo.getPoints() : 0;
        if (currentPoints < points) {
            // 积分不足时扣减到0，不扣成负数
            points = currentPoints;
        }
        if (points <= 0) {
            return; // 无积分可扣，直接返回
        }
        memberInfo.setPoints(currentPoints - points);
        memberInfoMapper.updateByUserId(memberInfo);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(-points); // 负数表示扣减
        record.setType("DEDUCT");
        record.setDescription(description != null ? description : "积分扣减");
        record.setCreateTime(new java.util.Date());
        pointsRecordMapper.insert(record);
    }

    @Override
    public List<PointsRecord> getPointsHistory(Integer userId) {
        return pointsRecordMapper.selectByUserId(userId);
    }

    @Override
    public UserRole getUserRole(Integer userId) {
        UserRole userRole = userRoleMapper.selectByUserId(userId);
        if (userRole == null) {
            throw new RuntimeException("用户权限信息不存在");
        }
        return userRole;
    }

    @Override
    @Transactional
    public void updateUserRole(Integer userId, UserRole userRole) {
        UserRole existing = userRoleMapper.selectByUserId(userId);
        if (existing == null) {
            throw new RuntimeException("用户权限信息不存在");
        }
        userRole.setUserId(userId);
        userRoleMapper.updateByUserId(userRole);
    }

    @Override
    public List<MemberType> getAllMemberTypes() {
        return memberTypeMapper.selectAll();
    }

    @Override
    public MemberType getMemberTypeByCode(String levelCode) {
        return memberTypeMapper.selectByCode(levelCode);
    }

    @Override
    @Transactional
    public void purchaseMember(Integer userId, String levelCode) {
        MemberType memberType = memberTypeMapper.selectByCode(levelCode);
        if (memberType == null) {
            throw new RuntimeException("会员类型不存在");
        }

        MemberInfo memberInfo = memberInfoMapper.selectByUserId(userId);
        if (memberInfo == null) {
            // 首次购买会员
            memberInfo = new MemberInfo();
            memberInfo.setUserId(userId);
            memberInfo.setMemberLevel(memberType.getLevelName());
            memberInfo.setPoints(memberType.getPointsBonus());
            memberInfo.setExpireTime(LocalDateTime.now().plusDays(memberType.getDurationDays()));
            memberInfoMapper.insert(memberInfo);
            addPoints(userId, memberType.getPointsBonus());
        } else {
            // 已是会员：检查是否过期
            if (memberInfo.getExpireTime() != null && memberInfo.getExpireTime().isAfter(LocalDateTime.now())) {
                // 会员未过期，不允许重复购买
                throw new RuntimeException("您已是" + memberInfo.getMemberLevel() + "，有效期至 " +
                    memberInfo.getExpireTime().toLocalDate() + "，到期前不可重复购买");
            }
            // 会员已过期，允许重新购买
            memberInfo.setMemberLevel(memberType.getLevelName());
            memberInfo.setPoints(memberType.getPointsBonus());
            memberInfo.setExpireTime(LocalDateTime.now().plusDays(memberType.getDurationDays()));
            memberInfoMapper.updateByUserId(memberInfo);
            addPoints(userId, memberType.getPointsBonus());
        }

        UserRole userRole = userRoleMapper.selectByUserId(userId);
        if (userRole != null) {
            userRole.setRoleCode("VIP");
            userRole.setRoleName("VIP会员");
            userRole.setPermissions("user:read,user:update,address:crud,vip:discount");
            userRoleMapper.updateByUserId(userRole);
        }
    }
}
