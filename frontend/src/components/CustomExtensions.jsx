import React, { useState, useEffect } from 'react';
import { getCustomExtensions, addCustomExtension, deleteCustomExtension } from '../services/api';

const CustomExtensions = () => {
  const [extensions, setExtensions] = useState([]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadExtensions();
  }, []);

  const loadExtensions = async () => {
    try {
      const data = await getCustomExtensions();
      setExtensions(data);
    } catch (error) {
      console.error('Failed to load custom extensions:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAdd = async () => {
    if (!inputValue.trim()) return;
    
    const trimmedValue = inputValue.trim().toLowerCase();
    
    // Check for duplicates locally
    if (extensions.some(ext => ext.name === trimmedValue)) {
      alert('이미 존재하는 확장자입니다.');
      return;
    }

    try {
      const newExtension = await addCustomExtension(trimmedValue);
      setExtensions(prev => [...prev, newExtension]);
      setInputValue('');
    } catch (error) {
      console.error('Failed to add extension:', error);
      alert('확장자 추가에 실패했습니다.');
    }
  };

  const handleDelete = async (name) => {
    if (!window.confirm(`"${name}" 확장자를 삭제하시겠습니까?`)) {
      return;
    }

    try {
      await deleteCustomExtension(name);
      setExtensions(prev => prev.filter(ext => ext.name !== name));
    } catch (error) {
      console.error('Failed to delete extension:', error);
      alert('확장자 삭제에 실패했습니다.');
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleAdd();
    }
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <div>
      <h2 className="section-title">커스텀 확장자</h2>
      <div className="custom-input-container">
        <input
          type="text"
          className="custom-input"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="확장자 입력"
          maxLength={20}
        />
        <button className="add-button" onClick={handleAdd}>
          +추가
        </button>
      </div>
      <div className="extensions-display">
        <div className="count-display">
          {extensions.length}/200
        </div>
        <div className="tags-container">
          {extensions.map(extension => (
            <span key={extension.name} className="tag">
              {extension.name}
              <button 
                className="tag-delete"
                onClick={() => handleDelete(extension.name)}
                title="삭제"
              >
                ×
              </button>
            </span>
          ))}
        </div>
      </div>
    </div>
  );
};

export default CustomExtensions;

