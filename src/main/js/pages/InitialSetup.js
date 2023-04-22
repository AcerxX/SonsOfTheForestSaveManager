import React, { useEffect, useState } from 'react';
import { InfoCircleOutlined } from '@ant-design/icons';
import { Button, Form, Input, Radio } from 'antd';
import Title from 'antd/es/typography/Title';
import axios from 'axios';

const InitialSetup = ({ userData, setUserData }) => {
    const [form] = Form.useForm();

    const onFinish = (values) => {
        console.log('Success:', values);

        axios
        .patch(`/rest/users/${userData.id}`, values)
        .then(({data}) => {
            setUserData(data);
        })
    };

    const onFinishFailed = (errorInfo) => {
        console.log('Failed:', errorInfo);
    };

    return <>
        <Title level={5} style={{ marginBottom: '2rem' }}>Initial Setup</Title>

        <Form
            form={form}
            layout="vertical"
            initialValues={userData}
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
            autoComplete="off"
        >
            <Form.Item
                label="Host ID"
                tooltip="This is the ID of the host folder you can find in Multiplayer folder."
                name="hostId"
                rules={[
                    {
                      required: true,
                      message: 'Please input your Host ID!',
                    },
                  ]}
            >
                <Input placeholder="input placeholder" />
            </Form.Item>

            <Form.Item
                label="Client ID"
                tooltip="This is the ID of the client folder you can find in MultiplayerClient folder."
                name="clientId"
                rules={[
                    {
                      required: true,
                      message: 'Please input your Client ID!',
                    },
                  ]}
            >
                <Input placeholder="input placeholder" />
            </Form.Item>

            <Form.Item>
                <Button type="primary" htmlType="submit" style={{width: '100%', marginTop: '2rem'}}>Start Syncing</Button>
            </Form.Item>
        </Form>
    </>
}

export default InitialSetup;