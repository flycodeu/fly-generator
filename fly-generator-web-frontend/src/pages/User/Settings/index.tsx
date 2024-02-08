import PictureUpload from '@/components/PictureUpload';
import {updateMyUserUsingPost, updateUserUsingPost} from '@/services/backend/userController';
import { useModel } from '@@/exports';
import { PageContainer } from '@ant-design/pro-components';
import '@umijs/max';
import { history } from '@umijs/max';
import { Button, Card, Form, Input, message } from 'antd';
import TextArea from 'antd/es/input/TextArea';
import React, { useEffect, useState } from 'react';

const UserSettingsPage: React.FC = () => {
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};
  const [userInfo, setUserInfo] = useState<API.UserUpdateMyRequest>();


  useEffect(() => {
    setUserInfo(currentUser); // 设置初始值
    console.log(userInfo?.userAvatar);
  }, [currentUser]); // 当 currentUser 发生变化时重新设置 userInfo

  const onFinish = async () => {
    try {
      await updateMyUserUsingPost({
        id: currentUser?.id,
        userName: userInfo?.userName,
        userAvatar: userInfo?.userAvatar,
        userProfile: userInfo?.userProfile,
      });
      setTimeout(() => {
        history.push('/');
        window.location.reload();
      }, 1000);
    } catch (e: any) {
      message.error(e.message);
    }
    console.log('ok');
  };

  return (
    <PageContainer content={' 个人信息'}>
      <Card>
        <Form
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 14 }}
          layout="horizontal"
          style={{ maxWidth: 600 }}
        >
          <Form.Item label="头像" >
            <PictureUpload
              biz="user_avatar"
              value={userInfo?.userAvatar}
              onChange={(url) => setUserInfo({ ...userInfo, userAvatar: url })}
            />
          </Form.Item>

          <Form.Item label="用户名">
            <Input
              value={userInfo?.userName}
              onChange={(e) => setUserInfo({ ...userInfo, userName: e.target.value })}
            />
          </Form.Item>

          <Form.Item label="用户简介">
            <TextArea
              value={userInfo?.userProfile}
              onChange={(e) => setUserInfo({ ...userInfo, userProfile: e.target.value })}
            />
          </Form.Item>

          <Form.Item wrapperCol={{ offset: 6, span: 16 }}>
            <Button type="primary" onClick={onFinish}>
              修改
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </PageContainer>
  );
};
export default UserSettingsPage;
