import {
  getGeneratorVoByIdUsingGet,
  useGeneratorUsingPost,
} from '@/services/backend/generatorController';
import { Link, useParams } from '@@/exports';
import { DownloadOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import {
  Button,
  Card,
  Col,
  Collapse,
  Form,
  Image,
  Input,
  message,
  Row,
  Space,
  Tag,
  Typography,
} from 'antd';
import { saveAs } from 'file-saver';
import React, { useEffect, useState } from 'react';

/**
 * 生成器使用页
 * @constructor
 */
const GeneratorUsePage: React.FC = () => {
  const { id } = useParams();
  const [form] = Form.useForm();
  const [data, setData] = useState<API.GeneratorVO>({});
  const [loading, setLoading] = useState<boolean>(true);
  const { initialState } = useModel('@@initialState');
  const [downLoading, setDownLoading] = useState<boolean>();
  const { currentUser } = initialState ?? {};
  const my = currentUser?.id === data?.userId;

  const models = data?.modelConfig?.models ?? [];
  const loadData = async () => {
    if (!id) {
      return;
    }
    setLoading(true);
    try {
      const res = await getGeneratorVoByIdUsingGet({ id });
      setData(res.data ?? {});
    } catch (e: any) {
      message.error('数据获取失败');
    }
    setLoading(false);
  };

  useEffect(() => {
    if (!id) {
      return;
    }
    loadData();
  }, [id]);

  const tagListView = (tags?: string[]) => {
    if (!tags) {
      return <></>;
    }
    return (
      <div style={{ marginBottom: 8 }}>
        {tags.map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </div>
    );
  };

  /**
   * 下载按钮
   */
  const downloadButton = data.distPath && currentUser && (
    <Button
      type={'primary'}
      icon={<DownloadOutlined />}
      loading={downLoading}
      onClick={async () => {
        setDownLoading(true);
        const values = form.getFieldsValue();

        // eslint-disable-next-line react-hooks/rules-of-hooks
        const blob = await useGeneratorUsingPost(
          {
            id: data.id,
            dataModel: values,
          },
          {
            responseType: 'blob',
          },
        );
        // 使用 file-saver 来保存文件
        const fullPath = data.distPath || '';
        saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
        setDownLoading(false);
      }}
    >
      生成代码
    </Button>
  );

  return (
    <PageContainer title={''} loading={loading}>
      <Card>
        <Row justify={'space-between'} gutter={[32, 32]}>
          <Col flex={'auto'}>
            <Space size={'large'} align={'center'}>
              <Typography.Title level={4}>{data.name}</Typography.Title>
              {tagListView(data.tags)}
            </Space>
            <Typography.Paragraph>项目描述：{data.description}</Typography.Paragraph>
            <Form form={form}>
              {models.map((model, index) => {
                if (model.groupKey) {
                  if (!model.models) {
                    return <></>;
                  }
                  return (
                    <Collapse
                      style={{ marginBottom: '24px' }}
                      key={index}
                      items={[
                        {
                          key: index,
                          label: model.groupName + '(分组)',
                          children: model.models?.map((subModel, index) => {

                            return (
                              <Form.Item
                                key={index}
                                label={subModel.fieldName}
                                // @ts-ignore
                                name={[model.groupKey, subModel.fieldName]}
                              >
                                <Input placeholder={subModel.description} />
                              </Form.Item>
                            );
                          }),
                        },
                      ]}
                      bordered={false}
                      defaultActiveKey={[index]}
                    />
                  );
                }
                return (
                  <Form.Item key={index} label={model.fieldName} name={model.fieldName}>
                    <Input placeholder={model.description} />
                  </Form.Item>
                );
              })}
            </Form>
            <div style={{ marginBottom: 24 }} />
            <Space size="middle">
              {downloadButton}
              <Link to={`/generator/detail/${id}`}>
                <Button>查看详情</Button>
              </Link>
            </Space>
          </Col>
          <Col flex="320px">
            <Image src={data.picture} />
          </Col>
        </Row>
      </Card>
      <div style={{ marginBottom: 24 }}></div>
    </PageContainer>
  );
};

export default GeneratorUsePage;
