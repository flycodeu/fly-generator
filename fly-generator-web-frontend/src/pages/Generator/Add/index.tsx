import FileUpload from '@/components/FileUpload';
import PictureUpload from '@/components/PictureUpload';
import { COS_HOST } from '@/constants';
import FileConfigForm from '@/pages/Generator/Add/components/FileConfigForm';
import ModelConfigForm from '@/pages/Generator/Add/components/ModelConfigForm';
import {
  addGeneratorUsingPost,
  editGeneratorUsingPost,
  getGeneratorVoByIdUsingGet,
} from '@/services/backend/generatorController';
import { useSearchParams } from '@@/exports';
import {
  ProCard,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  StepsForm,
} from '@ant-design/pro-components';
import { ProFormItem } from '@ant-design/pro-form';
import { ProFormInstance } from '@ant-design/pro-form/lib';
import { history } from '@umijs/max';
import { message, UploadFile } from 'antd';
import React, { useEffect, useRef, useState } from 'react';

/**
 * 生成代码生成器界面
 * @constructor
 */
const GeneratorAddPage: React.FC = () => {
  const formRef = useRef<ProFormInstance>();
  const [searchParams] = useSearchParams();
  const id = searchParams.get('id');
  const [oldData, setOldData] = useState<API.GeneratorEditRequest>();

  const loadData = async () => {
    if (!id) {
      return;
    }
    try {
      const res = await getGeneratorVoByIdUsingGet({ id });
      if (res.data) {
        const { distPath } = res.data ?? {};
        if (distPath) {
          // @ts-ignore
          res.data.distPath = [
            {
              uid: id,
              name: '文件' + id,
              status: 'done',
              url: COS_HOST + distPath,
              response: distPath,
            } as UploadFile,
          ];
        }
        setOldData(res.data);
      }
    } catch (e: any) {
      message.error('数据获取失败');
    }
  };

  useEffect(() => {
    if (!id) {
      return;
    }
    loadData();
  }, [id]);

  /**
   * 添加
   * @param values
   */
  const doAdd = async (values: API.GeneratorAddRequest) => {
    try {
      const res = await addGeneratorUsingPost(values);
      if (res.data) {
        message.success('创建成功');
        history.push(`/generator/detail/${res.data}`);
      }
    } catch (e: any) {
      message.error('创建失败' + e.message);
    }
  };

  /**
   * 修改
   * @param values
   */
  const doUpdate = async (values: API.GeneratorEditRequest) => {
    try {
      const res = await editGeneratorUsingPost(values);
      if (res.data) {
        message.success('更新成功');
        history.push(`/generator/detail/${id}`);
      }
    } catch (e: any) {
      message.error('更新失败' + e.message);
    }
  };

  const doSubmit = async (values: API.GeneratorAddRequest) => {
    // 数据转换
    if (!values.fileConfig) {
      values.fileConfig = {};
    }
    if (!values.modelConfig) {
      values.modelConfig = {};
    }
    if (values.distPath && values.distPath.length > 0) {
      // @ts-ignore
      values.distPath = values.distPath[0].response;
    }
    // 提交数据
    // 有id是修改，无id是创建
    if (id) {
      await doUpdate({
        id,
        ...values,
      });
    } else {
      await doAdd(values);
    }
  };
  // todo 采用中间保存，让用户不会丢失界面后再次输入相同的内容
  return (
    <ProCard>
      {(!id || oldData) && (
        <StepsForm<API.GeneratorAddRequest | API.GeneratorEditRequest>
          onFinish={doSubmit}
          formProps={{ initialValues: oldData }}
          formRef={formRef}
        >
          <StepsForm.StepForm<{
            name: string;
          }>
            name="base"
            title="基本信息"
          >
            <ProFormItem label="图片" name="picture">
              <PictureUpload biz="generator_picture" />
            </ProFormItem>
            <ProFormText name="name" label="名称" placeholder="请输入代码生成器名称" />
            <ProFormTextArea
              name="description"
              label="描述"
              width="lg"
              placeholder="请输入代码生成器描述"
            />
            <ProFormText
              name="basePackage"
              label="基础包名"
              width="lg"
              placeholder="请输入基础包名"
            />
            <ProFormText name="version" label="版本号" width="lg" placeholder="请输入版本号" />
            <ProFormText name="author" label="作者" width="lg" placeholder="请输入作者名" />
            <ProFormSelect label="标签" mode="tags" name="tags" placeholder="请输入标签列表" />
          </StepsForm.StepForm>
          {/*todo 模型配置*/}
          <StepsForm.StepForm
            name="modelConfig"
            title="模型配置"
            onFinish={async (values) => {
              console.log(values);
              return true;
            }}
          >
            <ModelConfigForm formRef={formRef} oldData={oldData} />
          </StepsForm.StepForm>
          {/*todo 文件配置*/}
          <StepsForm.StepForm name="fileConfig" title="文件配置">
            <FileConfigForm formRef={formRef} oldData={oldData}></FileConfigForm>
          </StepsForm.StepForm>
          <StepsForm.StepForm name="dist" title="生成器文件">
            <ProFormItem label={'产物包'} name={'distPath'}>
              <FileUpload
                biz={'generator_dist'}
                description={'请上传代码生成器压缩包'}
              ></FileUpload>
            </ProFormItem>
          </StepsForm.StepForm>
        </StepsForm>
      )}
    </ProCard>
  );
};

export default GeneratorAddPage;
