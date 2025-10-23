import React from 'react';
import FixedExtensions from './components/FixedExtensions';
import CustomExtensions from './components/CustomExtensions';
import './style.css';

function App() {
  return (
    <div className="App">
      <h1 className="page-title">파일 확장자 차단</h1>
      <p className="page-description">
        파일확장자에 따라 특정 형식의 파일을 첨부하거나 전송하지 못하도록 제한
      </p>
      <main>
        <div className="section">
          <FixedExtensions />
        </div>
        <div className="section">
          <CustomExtensions />
        </div>
      </main>
    </div>
  );
}

export default App;

