import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Avatar, message, Typography } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import './SelectUserPage.css';
import axios from 'axios';

const { Text, Link, Title } = Typography;

const SelectUserPage = ({ userSetter, ...props }) => {

  const [users, setUsers] = useState([]);

  useEffect(() => {
    axios.get(`/rest/users`)
      .then(({ data }) => {
        setUsers(data._embedded.users);
      });
  }, []);

  const handleClick = (username) => {
    axios
      .get(`/user/login/${username}`)
      .then((_) => {
        userSetter(username);
      })
  }

  return <>
    <Title level={5} style={{ marginBottom: '2rem' }}>Who are you MALAKA?</Title>

    <Row gutter={[16, 16]}>
      {users.map((user) => (
        <Col key={user.id}>
          <Card
            hoverable
            className="card"
            style={{ width: 150, borderRadius: '50%', overflow: 'hidden' }}
            bodyStyle={{ padding: 0 }}
            onClick={() => handleClick(user.username)}
          >
            <Avatar shape="circle" size={150} src={user.imageUrl} icon={<UserOutlined />} />
            <div className="overlay">
              <span style={{ color: 'white', fontSize: '16px' }}>{user.username}</span>
            </div>
          </Card>
        </Col>
      ))}
    </Row>
  </>
};

export default SelectUserPage;


