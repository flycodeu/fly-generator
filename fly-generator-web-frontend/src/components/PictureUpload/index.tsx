import { COS_HOST } from '@/constants';
import { uploadFileUsingPost } from '@/services/backend/fileController';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import '@umijs/max';
import { message, Upload, UploadProps } from 'antd';
import React, { useState } from 'react';

interface Props {
  biz: string;
  onChange?: (url: string) => void;
  value?: string;
}

/**
 * 文件上传
 * @param props
 * @constructor
 */
const PictureUpload: React.FC<Props> = (props) => {
  const { biz, value, onChange } = props;
  // 防止用户在加载的时候上传其他文件
  const [loading, setLoading] = useState<boolean>(false);
  const uploadProps: UploadProps = {
    name: 'file',
    multiple: false,
    maxCount: 1,
    listType: 'picture-card',
    showUploadList: false,
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
        const fullPath = COS_HOST + res.data;
        onChange?.(fullPath ?? '');
        fileObj.onSuccess(fullPath);
      } catch (e: any) {
        message.error('上传失败' + e.message);
        fileObj.onError(e);
      }
      setLoading(false);
    },
    onRemove() {},
  };

  /**
   * 上传按钮
   */
  const uploadButton = (
      <div>
        {loading ? <LoadingOutlined /> : <PlusOutlined />}
        <div style={{ marginTop: 8 }}>上传</div>
      </div>
  );

  return (
      <Upload {...uploadProps}>
        {value ? <img src={value} alt="picture" style={{ width: '100%' }} /> : uploadButton}
      </Upload>
  );
};
export default PictureUpload;
