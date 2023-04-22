import React from "react";
import { createRoot } from 'react-dom/client';
import { ConfigProvider, theme } from "antd";
import ErrorBoundary from "antd/es/alert/ErrorBoundary";
import Application from "./Application";

const container = document.getElementById('root');
const root = createRoot(container);
root.render(
    <React.StrictMode>
        <ConfigProvider theme={{
            algorithm: theme.darkAlgorithm
        }}>
            <ErrorBoundary>
                <Application/>
            </ErrorBoundary>
        </ConfigProvider>
    </React.StrictMode>
);