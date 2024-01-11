import { uploadFileUsingPost } from '@/services/backend/fileController';
import { InboxOutlined } from '@ant-design/icons';
import '@umijs/max';
import { message, UploadFile, UploadProps } from 'antd';
import Dragger from 'antd/es/upload/Dragger';
import React, { useState } from 'react';

interface Props {
  biz: string;
  onChange?: (fileList: UploadFile[]) => void;
  value?: UploadFile[];
  description?: string;
}

/**
 * 文件上传
 * @param props
 * @constructor
 */
const FileUpload: React.FC<Props> = (props) => {
  const { biz, value, description, onChange } = props;
  // 防止用户在加载的时候上传其他文件
  const [loading, setLoading] = useState<boolean>();
  const uploadProps: UploadProps = {
    name: 'file',
    multiple: false,
    maxCount: 1,
    listType: 'text',
    disabled: loading,
    fileList: value,
    onChange({ fileList }) {
      onChange?.(fileList);
    },
    customRequest: async (fileObj: any) => {
      setLoading(true);
      try {
        const res = await uploadFileUsingPost(
          {
            biz,
          },
          {},
          fileObj.file,
        );
        fileObj.onSuccess(res.data);
      } catch (e: any) {
        message.error('上传失败' + e.message);
        fileObj.onError(e);
      }
      setLoading(false);
    },
    onRemove() {},
  };
  return (
    <Dragger {...uploadProps}>
      <p className="ant-upload-drag-icon">
        <InboxOutlined />
      </p>
      <p className="ant-upload-text">点击或者拖拽上传</p>
      <p className="ant-upload-hint">{description}</p>
    </Dragger>
  );
};
export default FileUpload;
