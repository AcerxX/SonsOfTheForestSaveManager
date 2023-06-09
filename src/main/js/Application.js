import React, { useEffect, useState } from "react";
import SelectUserPage from "./pages/SelectUserPage";
import axios from "axios";
import InitialSetup from "./pages/InitialSetup";
import Title from "antd/es/typography/Title";

const Application = () => {
    const [loggedUser, setLoggedUser] = useState("");
    const [loading, setLoading] = useState(true);
    const [userData, setUserData] = useState({})

    useEffect(() => {
        axios
            .get("/user/info")
            .then(({ data }) => {
                if (data.length > 0) {
                    setLoggedUser(data);
                }

                setLoading(false);
            });
    }, []);

    useEffect(() => {
        if (loggedUser.length > 0) {
            updateUserData();
        }
    }, [loggedUser]);

    const updateUserData = () => {
        axios.get(`/rest/users/search/findTopByUsername?username=${loggedUser}`)
            .then(({ data }) => {
                setUserData(data);
            });
    };

    return <div style={{
        display: 'flex',
        // justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: 'black',
        flexDirection: 'column'
    }}>
        <img
            src="https://gamelevate.com/wp-content/uploads/2023/02/sons-of-the-forest.jpg"
            // width="200"
            height="200"
            alt="Sons of the Forest Logo"
            style={{ marginBottom: '2rem' }}
        />

        {loading &&
            <Title level={5}>Loading...</Title>
        }

        {(!loading && loggedUser.length === 0) &&
            <SelectUserPage userSetter={setLoggedUser} />
        }

        {(!loading && loggedUser.length > 0 && (userData.clientId === null || userData.hostId === null)) &&
            <InitialSetup userData={userData} setUserData={setUserData} />
        }


        {(!loading && loggedUser.length > 0 && Object.keys(userData).length > 0 && userData.clientId !== null && userData.hostId !== null) &&
            <>
                <Title level={4}>All good!</Title>
                <Title level={5}>Whenever you want to sync the save files, just open the console app (.jar file).</Title>
                <Title level={5}>You can close the browser window / tab.</Title>
            </>
        }

    </div>;
}

export default Application;