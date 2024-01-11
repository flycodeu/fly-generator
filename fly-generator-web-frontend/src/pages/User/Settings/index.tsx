import { PageContainer, ProForm, ProFormText } from '@ant-design/pro-components';
import '@umijs/max';
import { Card } from 'antd';
import React from 'react';

const UserSettingsPage: React.FC = () => {
  return (
    <PageContainer content={' 个人信息'}>
      <Card>
        <ProForm
          layout="vertical"
          submitter={{
            searchConfig: {
              submitText: '更新个人信息',
            },
          }}
        >
          <ProFormText name={'userAccount'} label={'账号'} disabled={true}></ProFormText>
          <ProFormText name={'username'} label={'昵称'}></ProFormText>
          <ProFormText name={'userProfile'} label={'个人简介'}></ProFormText>
          <ProFormText name={'userRole'} label={'用户角色'}></ProFormText>
        </ProForm>
      </Card>
    </PageContainer>
  );
};
export default UserSettingsPage;
