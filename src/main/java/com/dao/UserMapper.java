package com.dao;

import com.pojo.User;
import com.pojo.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    long countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int countByUsername(String username);

    int countByEmail(String email);

    User selectLogin(@Param("username") String username,@Param("password") String password);

    String selectQuestion(String username);

    int checkAnswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);

    int updateByUsername(@Param("username") String username,@Param("password") String passwordNew);

    int checkPassword(@Param("password") String password,@Param("userId") int userId);

    /**
     * 检查email是否与非本身的email相等
     * @param email
     * @param userId
     * @return
     */
    int checkEmailByUserId(@Param("email") String email,@Param("userId") int userId);
}