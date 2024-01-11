import { COS_HOST } from '@/constants';
import { testDownloadUsingGet, testUploadUsingPost } from '@/services/backend/fileController';
import { InboxOutlined } from '@ant-design/icons';
import '@umijs/max';
import { Button, Card, Divider, Flex, message, UploadProps } from 'antd';
import Dragger from 'antd/es/upload/Dragger';
import { saveAs } from 'file-saver';
import React, { useState } from 'react';

const TestFile: React.FC = () => {
  const [value, setValue] = useState<string>();
  const props: UploadProps = {
    name: 'file',
    multiple: false,
    maxCount: 1,
    customRequest: async (fileObj: any) => {
      try {
        const res = await testUploadUsingPost({}, fileObj.file);
        fileObj.onSuccess(res.data);
        setValue(res.data);
      } catch (e: any) {
        message.error('上传失败' + e.message);
        fileObj.onError(e);
      }
    },
    onRemove() {
      setValue(undefined);
    },
  };
  return (
    <Flex gap={16}>
      <Card title={'文件上传'}>
        <Dragger {...props}>
          <p className="ant-upload-drag-icon">
            <InboxOutlined />
          </p>
          <p className="ant-upload-text">Click or drag file to this area to upload</p>
          <p className="ant-upload-hint">
            Support for a single or bulk upload. Strictly prohibited from uploading company data or
            other banned files.
          </p>
        </Dragger>
      </Card>

      <Card title={'文件下载'}>
        <div>文件地址: {COS_HOST + value}</div>
        <Divider />
        <img src={COS_HOST + value} height={400} />
        <Divider />
        <Button
          onClick={async () => {
            const blob = await testDownloadUsingGet({ filepath: value }, { responseType: 'blob' });
            // 使用file-saver下载文件
            const fullPath = COS_HOST + value;
            saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
          }}
        >
          点击下载文件
        </Button>
      </Card>
    </Flex>
  );
};
export default TestFile;
